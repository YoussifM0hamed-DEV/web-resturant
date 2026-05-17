document.addEventListener('DOMContentLoaded', () => {
    // Register ScrollTrigger if needed (though we'll use stagger for entrance)
    // gsap.registerPlugin(ScrollTrigger);

    // Fade in and stagger entrance for key elements
    if (typeof gsap !== 'undefined') {
        // Hero section animations
        gsap.from('.hero h1', {
            opacity: 0,
            y: 30,
            duration: 1.2,
            ease: 'power3.out'
        });

        gsap.from('.hero p', {
            opacity: 0,
            y: 20,
            duration: 1.2,
            delay: 0.3,
            ease: 'power3.out'
        });

        gsap.from('.hero .btn', {
            opacity: 0,
            y: 20,
            duration: 1,
            delay: 0.6,
            stagger: 0.2,
            ease: 'power3.out'
        });

        // Menu cards stagger entrance
        gsap.from('.menu-card', {
            opacity: 0,
            y: 40,
            duration: 1,
            stagger: 0.1,
            ease: 'power2.out',
            scrollTrigger: {
                trigger: '.menu-grid, .row.g-4',
                start: 'top 85%'
            }
        });

        // Section titles entrance
        gsap.from('.section-title', {
            opacity: 0,
            x: -30,
            duration: 1,
            ease: 'power2.out',
            scrollTrigger: {
                trigger: '.section-title',
                start: 'top 90%'
            }
        });
    }
});
