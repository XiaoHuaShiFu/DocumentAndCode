chrome.app.runtime.onLaunched.addListener(function() {

  chrome.app.window.create('dhc.html', {
    'bounds': {
      'width': 1200,
      'height': 800
    }
  });
});