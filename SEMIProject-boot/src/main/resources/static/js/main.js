// carousel.js
document.addEventListener('DOMContentLoaded', function() {
    // Get carousel elements
    const slides = document.querySelectorAll('.carousel-slide');
    const prevBtn = document.querySelector('.prev-btn');
    const nextBtn = document.querySelector('.next-btn');
    const indicators = document.querySelectorAll('.indicator-btn');
    
    let currentSlide = 0;
    const slideCount = slides.length;
    let slideInterval;
    
    // Function to show specific slide
    function showSlide(index) {
        // Validate index
        if (index < 0) {
            index = slideCount - 1;
        } else if (index >= slideCount) {
            index = 0;
        }
        
        // Update current slide
        currentSlide = index;
        
        // Hide all slides and deactivate all indicators
        slides.forEach(slide => {
            slide.classList.remove('active');
        });
        
        indicators.forEach(indicator => {
            indicator.classList.remove('active');
        });
        
        // Show current slide and activate current indicator
        slides[currentSlide].classList.add('active');
        indicators[currentSlide].classList.add('active');
    }
    
    // Function to show next slide
    function nextSlide() {
        showSlide(currentSlide + 1);
    }
    
    // Function to show previous slide
    function prevSlide() {
        showSlide(currentSlide - 1);
    }
    
    // Event listeners for buttons
    if (prevBtn && nextBtn) {
        prevBtn.addEventListener('click', () => {
            prevSlide();
            resetInterval();
        });
        
        nextBtn.addEventListener('click', () => {
            nextSlide();
            resetInterval();
        });
    }
    
    // Event listeners for indicators
    indicators.forEach((indicator, index) => {
        indicator.addEventListener('click', () => {
            showSlide(index);
            resetInterval();
        });
    });
    
    // Automatic slide change
    function startInterval() {
        slideInterval = setInterval(nextSlide, 5000); // Change slide every 5 seconds
    }
    
    // Reset interval after manual navigation
    function resetInterval() {
        clearInterval(slideInterval);
        startInterval();
    }
    
    // Start the carousel
    startInterval();
    
    // Handle visibility change to pause/resume carousel when tab is inactive
    document.addEventListener('visibilitychange', () => {
        if (document.hidden) {
            clearInterval(slideInterval);
        } else {
            startInterval();
        }
    });
    
    // Add keyboard navigation
    document.addEventListener('keydown', (e) => {
        if (e.key === 'ArrowLeft') {
            prevSlide();
            resetInterval();
        } else if (e.key === 'ArrowRight') {
            nextSlide();
            resetInterval();
        }
    });
    
    // Add touch navigation
    const carouselContainer = document.querySelector('.carousel-container');
    let touchStartX = 0;
    let touchEndX = 0;
    
    if (carouselContainer) {
        carouselContainer.addEventListener('touchstart', (e) => {
            touchStartX = e.changedTouches[0].screenX;
        }, { passive: true });
        
        carouselContainer.addEventListener('touchend', (e) => {
            touchEndX = e.changedTouches[0].screenX;
            handleSwipe();
        }, { passive: true });
    }
    
    function handleSwipe() {
        const swipeThreshold = 50; // Minimum distance for swipe
        
        if (touchEndX < touchStartX - swipeThreshold) {
            // Swipe left, go to next slide
            nextSlide();
            resetInterval();
        } else if (touchEndX > touchStartX + swipeThreshold) {
            // Swipe right, go to previous slide
            prevSlide();
            resetInterval();
        }
    }
    
    // Add hover pause
    const carousel = document.querySelector('.hero-carousel');
    
    if (carousel) {
        carousel.addEventListener('mouseenter', () => {
            clearInterval(slideInterval);
        });
        
        carousel.addEventListener('mouseleave', () => {
            startInterval();
        });
    }
    
    // Initialize with first slide active
    showSlide(0);
    
    // Update product badge colors based on content
    const badges = document.querySelectorAll('.product-badge');
    
    badges.forEach(badge => {
        if (badge.textContent.includes('나눔완료')) {
            badge.style.backgroundColor = '#6c5ce7'; // Purple for completed
        } else if (badge.textContent.includes('나눔중')) {
            badge.style.backgroundColor = '#00b894'; // Green for active
        }
    });
});