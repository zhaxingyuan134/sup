/**
 * 响应式设计增强脚本
 * 优化不同设备上的显示效果和用户体验
 */

class ResponsiveManager {
    constructor() {
        this.breakpoints = {
            xs: 576,
            sm: 768,
            md: 992,
            lg: 1200,
            xl: 1400
        };
        
        this.currentBreakpoint = this.getCurrentBreakpoint();
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.handleInitialLoad();
        this.optimizeForDevice();
    }

    setupEventListeners() {
        // 窗口大小变化监听
        window.addEventListener('resize', this.debounce(() => {
            const newBreakpoint = this.getCurrentBreakpoint();
            if (newBreakpoint !== this.currentBreakpoint) {
                this.currentBreakpoint = newBreakpoint;
                this.handleBreakpointChange();
            }
            this.handleResize();
        }, 250));

        // 设备方向变化监听
        window.addEventListener('orientationchange', () => {
            setTimeout(() => {
                this.handleOrientationChange();
            }, 100);
        });

        // 触摸设备优化
        if (this.isTouchDevice()) {
            this.optimizeForTouch();
        }
    }

    getCurrentBreakpoint() {
        const width = window.innerWidth;
        if (width < this.breakpoints.xs) return 'xs';
        if (width < this.breakpoints.sm) return 'sm';
        if (width < this.breakpoints.md) return 'md';
        if (width < this.breakpoints.lg) return 'lg';
        return 'xl';
    }

    handleInitialLoad() {
        document.body.classList.add(`breakpoint-${this.currentBreakpoint}`);
        
        // 移动设备优化
        if (this.isMobile()) {
            document.body.classList.add('mobile-device');
            this.optimizeForMobile();
        }

        // 平板设备优化
        if (this.isTablet()) {
            document.body.classList.add('tablet-device');
            this.optimizeForTablet();
        }
    }

    handleBreakpointChange() {
        // 移除旧的断点类
        document.body.classList.forEach(className => {
            if (className.startsWith('breakpoint-')) {
                document.body.classList.remove(className);
            }
        });
        
        // 添加新的断点类
        document.body.classList.add(`breakpoint-${this.currentBreakpoint}`);
        
        // 触发自定义事件
        window.dispatchEvent(new CustomEvent('breakpointChange', {
            detail: { breakpoint: this.currentBreakpoint }
        }));

        // 重新布局
        this.adjustLayout();
    }

    handleResize() {
        this.adjustTables();
        this.adjustCharts();
        this.adjustModals();
        this.adjustSidebar();
    }

    handleOrientationChange() {
        // 重新计算布局
        setTimeout(() => {
            this.handleResize();
        }, 300);
    }

    adjustLayout() {
        // 调整卡片布局
        this.adjustCards();
        
        // 调整表格布局
        this.adjustTables();
        
        // 调整图表布局
        this.adjustCharts();
        
        // 调整导航布局
        this.adjustNavigation();
    }

    adjustCards() {
        const cards = document.querySelectorAll('.card, .stats-card');
        cards.forEach(card => {
            if (this.isMobile()) {
                card.classList.add('mobile-card');
            } else {
                card.classList.remove('mobile-card');
            }
        });
    }

    adjustTables() {
        const tables = document.querySelectorAll('table');
        tables.forEach(table => {
            const wrapper = table.closest('.table-responsive');
            if (!wrapper && this.isMobile()) {
                // 为移动设备添加响应式包装
                const responsiveWrapper = document.createElement('div');
                responsiveWrapper.className = 'table-responsive';
                table.parentNode.insertBefore(responsiveWrapper, table);
                responsiveWrapper.appendChild(table);
            }
            
            // 移动设备表格优化
            if (this.isMobile()) {
                this.optimizeTableForMobile(table);
            }
        });
    }

    optimizeTableForMobile(table) {
        // 添加移动设备表格样式
        table.classList.add('mobile-table');
        
        // 为长文本添加省略号
        const cells = table.querySelectorAll('td');
        cells.forEach(cell => {
            if (cell.textContent.length > 20) {
                cell.classList.add('text-truncate');
                cell.title = cell.textContent;
            }
        });
    }

    adjustCharts() {
        // 重新调整Chart.js图表大小
        if (window.Chart && window.Chart.instances) {
            Object.values(window.Chart.instances).forEach(chart => {
                if (chart && chart.resize) {
                    chart.resize();
                }
            });
        }
    }

    adjustModals() {
        const modals = document.querySelectorAll('.modal');
        modals.forEach(modal => {
            if (this.isMobile()) {
                modal.classList.add('modal-fullscreen-sm-down');
            } else {
                modal.classList.remove('modal-fullscreen-sm-down');
            }
        });
    }

    adjustSidebar() {
        const sidebar = document.querySelector('.sidebar');
        if (!sidebar) return;

        if (this.isMobile()) {
            // 移动设备：侧边栏折叠
            sidebar.classList.add('sidebar-mobile');
            this.createMobileToggle();
        } else {
            // 桌面设备：侧边栏展开
            sidebar.classList.remove('sidebar-mobile', 'sidebar-collapsed');
        }
    }

    adjustNavigation() {
        const navbar = document.querySelector('.navbar');
        if (!navbar) return;

        if (this.isMobile()) {
            navbar.classList.add('navbar-mobile');
        } else {
            navbar.classList.remove('navbar-mobile');
        }
    }

    createMobileToggle() {
        if (document.querySelector('.mobile-sidebar-toggle')) return;

        const toggle = document.createElement('button');
        toggle.className = 'btn btn-outline-primary mobile-sidebar-toggle d-md-none';
        toggle.innerHTML = '<i class="fas fa-bars"></i>';
        toggle.style.cssText = `
            position: fixed;
            top: 20px;
            left: 20px;
            z-index: 1050;
            border-radius: 50%;
            width: 50px;
            height: 50px;
        `;

        toggle.addEventListener('click', () => {
            const sidebar = document.querySelector('.sidebar');
            if (sidebar) {
                sidebar.classList.toggle('sidebar-show');
            }
        });

        document.body.appendChild(toggle);
    }

    optimizeForMobile() {
        // 添加移动设备专用样式
        const mobileStyles = `
            .mobile-device .container-fluid {
                padding-left: 10px;
                padding-right: 10px;
            }
            
            .mobile-device .card {
                margin-bottom: 1rem;
                border-radius: 10px;
            }
            
            .mobile-device .btn {
                min-height: 44px;
                font-size: 16px;
            }
            
            .mobile-device .form-control {
                min-height: 44px;
                font-size: 16px;
            }
            
            .mobile-device .table {
                font-size: 14px;
            }
            
            .mobile-device .modal-dialog {
                margin: 10px;
            }
            
            .sidebar-mobile {
                position: fixed;
                left: -100%;
                transition: left 0.3s ease;
                z-index: 1040;
            }
            
            .sidebar-mobile.sidebar-show {
                left: 0;
            }
            
            .mobile-card {
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            }
        `;
        
        this.addStyles('mobile-styles', mobileStyles);
    }

    optimizeForTablet() {
        const tabletStyles = `
            .tablet-device .container-fluid {
                padding-left: 15px;
                padding-right: 15px;
            }
            
            .tablet-device .card {
                margin-bottom: 1.5rem;
            }
        `;
        
        this.addStyles('tablet-styles', tabletStyles);
    }

    optimizeForTouch() {
        // 触摸设备优化
        document.body.classList.add('touch-device');
        
        const touchStyles = `
            .touch-device .btn {
                min-height: 44px;
                min-width: 44px;
            }
            
            .touch-device .nav-link {
                padding: 12px 16px;
            }
            
            .touch-device .dropdown-item {
                padding: 12px 16px;
            }
            
            .touch-device .form-control {
                min-height: 44px;
            }
        `;
        
        this.addStyles('touch-styles', touchStyles);
    }

    optimizeForDevice() {
        // 根据设备类型进行优化
        const userAgent = navigator.userAgent.toLowerCase();
        
        if (userAgent.includes('iphone') || userAgent.includes('ipad')) {
            document.body.classList.add('ios-device');
            this.optimizeForIOS();
        } else if (userAgent.includes('android')) {
            document.body.classList.add('android-device');
            this.optimizeForAndroid();
        }
    }

    optimizeForIOS() {
        // iOS设备特殊优化
        const iosStyles = `
            .ios-device input[type="date"],
            .ios-device input[type="time"],
            .ios-device input[type="datetime-local"] {
                min-height: 44px;
            }
        `;
        
        this.addStyles('ios-styles', iosStyles);
    }

    optimizeForAndroid() {
        // Android设备特殊优化
        const androidStyles = `
            .android-device .form-control {
                background-color: transparent;
            }
        `;
        
        this.addStyles('android-styles', androidStyles);
    }

    addStyles(id, css) {
        if (document.getElementById(id)) return;
        
        const style = document.createElement('style');
        style.id = id;
        style.textContent = css;
        document.head.appendChild(style);
    }

    // 工具方法
    isMobile() {
        return window.innerWidth < this.breakpoints.md;
    }

    isTablet() {
        return window.innerWidth >= this.breakpoints.md && window.innerWidth < this.breakpoints.lg;
    }

    isTouchDevice() {
        return 'ontouchstart' in window || navigator.maxTouchPoints > 0;
    }

    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
}

// 性能优化管理器
class PerformanceManager {
    constructor() {
        this.init();
    }

    init() {
        this.optimizeImages();
        this.lazyLoadContent();
        this.optimizeAnimations();
        this.setupIntersectionObserver();
    }

    optimizeImages() {
        // 图片懒加载
        const images = document.querySelectorAll('img[data-src]');
        const imageObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.removeAttribute('data-src');
                    imageObserver.unobserve(img);
                }
            });
        });

        images.forEach(img => imageObserver.observe(img));
    }

    lazyLoadContent() {
        // 内容懒加载
        const lazyElements = document.querySelectorAll('[data-lazy]');
        const contentObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const element = entry.target;
                    const loadFunction = window[element.dataset.lazy];
                    if (typeof loadFunction === 'function') {
                        loadFunction(element);
                    }
                    contentObserver.unobserve(element);
                }
            });
        });

        lazyElements.forEach(el => contentObserver.observe(el));
    }

    optimizeAnimations() {
        // 检查用户是否偏好减少动画
        if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
            document.body.classList.add('reduce-motion');
            
            const reduceMotionStyles = `
                .reduce-motion * {
                    animation-duration: 0.01ms !important;
                    animation-iteration-count: 1 !important;
                    transition-duration: 0.01ms !important;
                }
            `;
            
            const style = document.createElement('style');
            style.textContent = reduceMotionStyles;
            document.head.appendChild(style);
        }
    }

    setupIntersectionObserver() {
        // 为动画元素设置观察器
        const animatedElements = document.querySelectorAll('[data-animate]');
        const animationObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const element = entry.target;
                    const animation = element.dataset.animate;
                    element.classList.add('animate__animated', `animate__${animation}`);
                    animationObserver.unobserve(element);
                }
            });
        });

        animatedElements.forEach(el => animationObserver.observe(el));
    }
}

// 初始化
document.addEventListener('DOMContentLoaded', () => {
    window.responsiveManager = new ResponsiveManager();
    window.performanceManager = new PerformanceManager();
});

// 导出到全局
window.ResponsiveManager = ResponsiveManager;
window.PerformanceManager = PerformanceManager;