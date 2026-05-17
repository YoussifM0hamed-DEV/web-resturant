const dot = document.getElementById('c-dot');
const ring = document.getElementById('c-ring');
let mx=0,my=0,rx=0,ry=0;

document.addEventListener('mousemove', e => { mx=e.clientX; my=e.clientY; });

// dot follows instantly, ring lerps smoothly
function lerp(a,b,t){ return a+(b-a)*t }
function loop(){
  rx = lerp(rx,mx,0.1);
  ry = lerp(ry,my,0.1);
  if (dot) {
    dot.style.left  = mx+'px';
    dot.style.top   = my+'px';
  }
  if (ring) {
    ring.style.left = rx+'px';
    ring.style.top  = ry+'px';
  }
  requestAnimationFrame(loop);
}
loop();

// hover states — add to all interactive elements
function initHovers() {
  document.querySelectorAll('a, button, .menu-card, .btn, .qty-btn, .category-pill, .tab-btn').forEach(el => {
    el.addEventListener('mouseenter', () => {
      if (ring) {
        ring.style.width  = '64px';
        ring.style.height = '64px';
        ring.style.borderColor = 'rgba(201,168,76,0.9)';
        ring.style.background  = 'rgba(201,168,76,0.07)';
      }
      if (dot) dot.style.opacity = '0';
    });
    el.addEventListener('mouseleave', () => {
      if (ring) {
        ring.style.width  = '38px';
        ring.style.height = '38px';
        ring.style.borderColor = 'rgba(201,168,76,0.55)';
        ring.style.background  = 'transparent';
      }
      if (dot) dot.style.opacity = '1';
    });
  });

  // image hover: ring shows "VIEW" text
  document.querySelectorAll('img, .gallery-img, .menu-card-img').forEach(img => {
    img.addEventListener('mouseenter', () => {
      if (ring) {
        ring.style.width='70px'; ring.style.height='70px';
        ring.innerHTML='VIEW';
      }
    });
    img.addEventListener('mouseleave', () => {
      if (ring) {
        ring.style.width='38px'; ring.style.height='38px';
        ring.innerHTML='';
      }
    });
  });
}

initHovers();

// Re-initialize hovers if content changes (e.g. AJAX or dynamic lists)
const observer = new MutationObserver(initHovers);
observer.observe(document.body, { childList: true, subtree: true });

// click ripple on dot
document.addEventListener('mousedown', () => {
  if (dot) {
    dot.style.transform = 'translate(-50%,-50%) scale(0.4)';
    setTimeout(()=> dot.style.transform='translate(-50%,-50%) scale(1)', 150);
  }
});
