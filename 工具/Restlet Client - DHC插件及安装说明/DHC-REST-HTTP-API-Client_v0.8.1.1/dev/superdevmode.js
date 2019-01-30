//this script allows superdevmode in running chrome extension
(function(){

    var script = document.createElement("script");
    chrome.storage.local.get("superdevmode",
    		function(o) {
    			if (o && o.superdevmode !=='on') {
    		        script.src = '/devhttpclient/devhttpclient.nocache.js';
    		        document.documentElement.appendChild(script);
    		        return;    				
    			}
    console.log("Super dev mode enabled");
    var server = sessionStorage.getItem("superdevmodeHost");
    if(!server){
        server = "http://localhost:9876";
        console.log("superdevmodeHost is not set, using default " + server);
    } else {
        console.log("Using codeserver at " + server);
    }
    var xhr = new XMLHttpRequest();
    xhr.open("get",server + '/recompile/devhttpclient?user.agent=safari&_callback=c');
    xhr.onload = function(){
        var responseText = xhr.responseText;
        var result = responseText.substring(2,responseText.length -3);
        try {
            var parse = JSON.parse(result);
        } catch(e){
            alert("Failed to parse server response,check codeserver logs")
            return;
        }
        if(parse && parse.status === "ok"){
            var script = document.createElement("script");
            script.src=server + '/devhttpclient/devhttpclient.nocache.js';
            document.documentElement.appendChild(script)
        } else {
            var frame = document.createElement("iframe");
            frame.style.width = "100%";
            frame.style.height = "900px";
            frame.src = server + "/log/devhttpclient";

            document.body.appendChild(frame);

        }
    };
    xhr.onerror = function(){
        alert("Codeserver " + server + " is not running");
    };
    xhr.send();

	}); 

})();
function loadapp(result){
    if(result.status === 'ok'){

    } else {
        alert("Compilation failed, check superdevmode console");
    }
}
