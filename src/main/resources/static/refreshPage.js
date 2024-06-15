// console.log("This console.log was called with the onload event");
// alert("This alert box was called with the onload event");
function autoRefresh() {
    window.location = window.location.href;
}

setInterval('autoRefresh()', 1000);