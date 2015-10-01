function CanvasParser(canvas) {
    canvas.width = window.innerWidth - 100;
    canvas.height = window.innerHeight - 100;

    var ctx = canvas.getContext("2d");
    var parser = new GestureParser({
        onGesture: function (type, points) {
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            ctx.fillStyle = '#000000';
            ctx.strokeStyle = '#000000';

            ctx.beginPath();
            points.forEach(function (point) {
                ctx.lineTo(point.x, point.y);
            });
            ctx.stroke();

            ctx.font = '20pt Sans-Serif';
            ctx.fillText(type, 2, canvas.height - 8);
            console.log(type);
        }
    });
    canvas.addEventListener("mousedown", function (event) {
        event.preventDefault();
        parser.onTouchDown({ x: event.clientX, y: event.clientY })
    }, false);
    canvas.addEventListener("mousemove", function (event) {
        event.preventDefault();
        parser.onTouchMoved({ x: event.clientX, y: event.clientY })
    }, false);
    canvas.addEventListener("mouseup", function (event) {
        event.preventDefault();
        parser.onTouchUp({ x: event.clientX, y: event.clientY })
    }, false);
}
