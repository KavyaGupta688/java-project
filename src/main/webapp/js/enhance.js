/* 3D tilt cards: hover tilt + cursor-following glow */
(function () {
    document.querySelectorAll('.card, .stat-tile').forEach(function (el) {
        var glow = document.createElement('div');
        glow.className = 'card-glow';
        el.appendChild(glow);
        el.classList.add('tilt-card');

        el.addEventListener('mousemove', function (e) {
            var r = el.getBoundingClientRect();
            var x = (e.clientX - r.left) / r.width;
            var y = (e.clientY - r.top) / r.height;
            el.style.setProperty('--mx', (x * 100) + '%');
            el.style.setProperty('--my', (y * 100) + '%');
            var maxTilt = 3.5;
            var rx = (0.5 - y) * maxTilt;
            var ry = (x - 0.5) * maxTilt;
            el.style.transform = 'perspective(1100px) rotateX(' + rx.toFixed(2) + 'deg) rotateY(' + ry.toFixed(2) + 'deg) translateY(-3px)';
        });

        el.addEventListener('mouseleave', function () {
            el.style.transform = '';
        });
    });
})();
