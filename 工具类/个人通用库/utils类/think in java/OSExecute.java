package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 描述: 在命令行执行命令
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-14 17:56
 */
public class OSExecute {

    public static void main(String[] args) {
        command("mysql --version");
    }

    private static class OSExecuteException extends RuntimeException {
        public OSExecuteException(String why) {
            super(why);
        }
    }

    public static void command(String command) {
        boolean err = false;
        try {
            Process process = new ProcessBuilder(command.split(" ")).start();
            BufferedReader results = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String s;
            while ((s = results.readLine()) != null) {
                System.out.println(s);
            }
            BufferedReader errors = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            while ((s = errors.readLine()) != null) {
                System.err.println(s);
                err = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (err) {
            throw new OSExecuteException("Errors executing " + command);
        }
    }

}
