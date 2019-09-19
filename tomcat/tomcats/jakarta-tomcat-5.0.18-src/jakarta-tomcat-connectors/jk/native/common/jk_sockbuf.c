/* ========================================================================= *
 *                                                                           *
 *                 The Apache Software License,  Version 1.1                 *
 *                                                                           *
 *          Copyright (c) 1999-2003 The Apache Software Foundation.          *
 *                           All rights reserved.                            *
 *                                                                           *
 * ========================================================================= *
 *                                                                           *
 * Redistribution and use in source and binary forms,  with or without modi- *
 * fication, are permitted provided that the following conditions are met:   *
 *                                                                           *
 * 1. Redistributions of source code  must retain the above copyright notice *
 *    notice, this list of conditions and the following disclaimer.          *
 *                                                                           *
 * 2. Redistributions  in binary  form  must  reproduce the  above copyright *
 *    notice,  this list of conditions  and the following  disclaimer in the *
 *    documentation and/or other materials provided with the distribution.   *
 *                                                                           *
 * 3. The end-user documentation  included with the redistribution,  if any, *
 *    must include the following acknowlegement:                             *
 *                                                                           *
 *       "This product includes  software developed  by the Apache  Software *
 *        Foundation <http://www.apache.org/>."                              *
 *                                                                           *
 *    Alternately, this acknowlegement may appear in the software itself, if *
 *    and wherever such third-party acknowlegements normally appear.         *
 *                                                                           *
 * 4. The names  "The  Jakarta  Project",  "Jk",  and  "Apache  Software     *
 *    Foundation"  must not be used  to endorse or promote  products derived *
 *    from this  software without  prior  written  permission.  For  written *
 *    permission, please contact <apache@apache.org>.                        *
 *                                                                           *
 * 5. Products derived from this software may not be called "Apache" nor may *
 *    "Apache" appear in their names without prior written permission of the *
 *    Apache Software Foundation.                                            *
 *                                                                           *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES *
 * INCLUDING, BUT NOT LIMITED TO,  THE IMPLIED WARRANTIES OF MERCHANTABILITY *
 * AND FITNESS FOR  A PARTICULAR PURPOSE  ARE DISCLAIMED.  IN NO EVENT SHALL *
 * THE APACHE  SOFTWARE  FOUNDATION OR  ITS CONTRIBUTORS  BE LIABLE  FOR ANY *
 * DIRECT,  INDIRECT,   INCIDENTAL,  SPECIAL,  EXEMPLARY,  OR  CONSEQUENTIAL *
 * DAMAGES (INCLUDING,  BUT NOT LIMITED TO,  PROCUREMENT OF SUBSTITUTE GOODS *
 * OR SERVICES;  LOSS OF USE,  DATA,  OR PROFITS;  OR BUSINESS INTERRUPTION) *
 * HOWEVER CAUSED AND  ON ANY  THEORY  OF  LIABILITY,  WHETHER IN  CONTRACT, *
 * STRICT LIABILITY, OR TORT  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN *
 * ANY  WAY  OUT OF  THE  USE OF  THIS  SOFTWARE,  EVEN  IF  ADVISED  OF THE *
 * POSSIBILITY OF SUCH DAMAGE.                                               *
 *                                                                           *
 * ========================================================================= *
 *                                                                           *
 * This software  consists of voluntary  contributions made  by many indivi- *
 * duals on behalf of the  Apache Software Foundation.  For more information *
 * on the Apache Software Foundation, please see <http://www.apache.org/>.   *
 *                                                                           *
 * ========================================================================= */

/***************************************************************************
 * Description: Simple buffer object to handle buffered socket IO          *
 * Author:      Gal Shachor <shachor@il.ibm.com>                           *
 * Version:     $Revision: 1.8 $                                           *
 ***************************************************************************/

#include "jk_global.h"
#include "jk_sockbuf.h"

static int fill_buffer(jk_sockbuf_t *sb);

int jk_sb_open(jk_sockbuf_t *sb,
               int sd)
{
    if(sb && sd >= 0) {
        sb->end   = 0;
        sb->start = 0;
        sb->sd    = sd;
        return JK_TRUE;
    }

    return JK_FALSE;
}

int jk_sb_write(jk_sockbuf_t *sb,
                const void *buf, 
                unsigned sz)
{
    if(sb && buf && sz) {
        if((SOCKBUF_SIZE - sb->end) >= sz) {
            memcpy(sb->buf + sb->end, buf, sz);
            sb->end += sz;
        } else {
            if(!jk_sb_flush(sb)) {
                return JK_FALSE;
            }
            if(sz > SOCKBUF_SIZE) {
                return (send(sb->sd, (char *)buf, sz, 0) == (int)sz);
            } 
            
            memcpy(sb->buf + sb->end, buf, sz);
            sb->end += sz;
        }

        return JK_TRUE;
    }

    return JK_FALSE;
}

int jk_sb_flush(jk_sockbuf_t *sb)
{
    if(sb) {
        int save_out = sb->end;
        sb->end = sb->start = 0;
        if(save_out) {            
            return send(sb->sd, sb->buf, save_out, 0) == save_out;
        }
        return JK_TRUE;
    }

    return JK_FALSE;
}


int jk_sb_read(jk_sockbuf_t *sb,
               char **buf, 
               unsigned sz,
               unsigned *ac)
{
    if(sb && buf && ac) {
        unsigned avail;

        *ac = 0;
        *buf = NULL;

        if(sb->end == sb->start) {
            sb->end = sb->start = 0;
            if(fill_buffer(sb) < 0) {
                return JK_FALSE;
            }
        }
        
        *buf = sb->buf + sb->start;
        avail = sb->end - sb->start;
        if(avail > sz) {
            *ac = sz;
        } else {
            *ac = avail;
        }
        sb->start += *ac;

        return JK_TRUE;
    }

    return JK_FALSE;
}

int jk_sb_gets(jk_sockbuf_t *sb,
               char **ps)
{
    int ret;
    if(sb) {
        while(1) {
            unsigned i;
            for(i = sb->start ; i < sb->end ; i++) {
                if(JK_LF == sb->buf[i]) {
                    if(i > sb->start && JK_CR == sb->buf[i - 1]) {
                        sb->buf[i - 1] = '\0';
                    } else {
                        sb->buf[i] = '\0';
                    }
                    *ps = sb->buf + sb->start;
                    sb->start = (i + 1);
                    return JK_TRUE;
                }
            }
            if((ret = fill_buffer(sb)) < 0) {
                return JK_FALSE;
            } else if (ret == 0) {
                *ps = sb->buf + sb->start;
               if ((SOCKBUF_SIZE - sb->end) > 0) {
                    sb->buf[sb->end] = '\0';
                } else {
                    sb->buf[sb->end-1] = '\0';
                }
                return JK_TRUE;
            }
        }
    }

    return JK_FALSE;
}

/*
 * Read data from the socket into the associated buffer, and update the
 * start and end indices.  May move the data currently in the buffer.  If
 * new data is read into the buffer (or if it is already full), returns 1.
 * If EOF is received on the socket, returns 0.  In case of error returns
 * -1.  
 */
static int fill_buffer(jk_sockbuf_t *sb)
{
    int ret;
    
    /*
     * First move the current data to the beginning of the buffer
     */
    if(sb->start < sb->end) {
        if(sb->start > 0) {
            unsigned to_copy = sb->end - sb->start;
            memmove(sb->buf, sb->buf + sb->start, to_copy);
            sb->start = 0;
            sb->end = to_copy;
        }
    } else {
        sb->start = sb->end = 0;
    }
    
    /*
     * In the unlikely case where the buffer is already full, we won't be
     * reading anything and we'd be calling recv with a 0 count.  
     */
    if ((SOCKBUF_SIZE - sb->end) > 0) {
	/*
	 * Now, read more data
	 */
	ret = recv(sb->sd, 
		   sb->buf + sb->end, 
		   SOCKBUF_SIZE - sb->end, 0);   
	
	/* 0 is EOF/SHUTDOWN, -1 is SOCK_ERROR */
	if (ret <= 0) {
	    return ret;
	} 
	
	sb->end += ret;
    }
    
    return 1;
}
