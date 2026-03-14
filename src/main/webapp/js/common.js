/**
 * 超市积分管理系统 - 通用JavaScript库
 * 提供统一的前端交互功能和用户体验优化
 */

// 全局配置
const APP_CONFIG = {
    // API基础路径
    API_BASE: window.location.origin + '/sup',
    
    // 默认配置
    DEFAULTS: {
        TOAST_DURATION: 3000,
        LOADING_DELAY: 300,
        ANIMATION_DURATION: 300,
        DEBOUNCE_DELAY: 300
    },
    
    // 响应式断点
    BREAKPOINTS: {
        XS: 576,
        SM: 768,
        MD: 992,
        LG: 1200,
        XL: 1400
    }
};

// 工具函数
const Utils = {
    /**
     * 防抖函数
     */
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
    },

    /**
     * 节流函数
     */
    throttle(func, limit) {
        let inThrottle;
        return function() {
            const args = arguments;
            const context = this;
            if (!inThrottle) {
                func.apply(context, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    },

    /**
     * 格式化数字
     */
    formatNumber(num, decimals = 2) {
        if (isNaN(num)) return '0';
        return parseFloat(num).toLocaleString('zh-CN', {
            minimumFractionDigits: decimals,
            maximumFractionDigits: decimals
        });
    },

    /**
     * 格式化日期
     */
    formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
        if (!date) return '';
        const d = new Date(date);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        const hours = String(d.getHours()).padStart(2, '0');
        const minutes = String(d.getMinutes()).padStart(2, '0');
        const seconds = String(d.getSeconds()).padStart(2, '0');
        
        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day)
            .replace('HH', hours)
            .replace('mm', minutes)
            .replace('ss', seconds);
    },

    /**
     * 获取URL参数
     */
    getUrlParam(name) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(name);
    },

    /**
     * 检查是否为移动设备
     */
    isMobile() {
        return window.innerWidth < APP_CONFIG.BREAKPOINTS.MD;
    },

    /**
     * 生成随机ID
     */
    generateId(prefix = 'id') {
        return prefix + '_' + Math.random().toString(36).substr(2, 9);
    }
};

// 通知系统
const Toast = {
    /**
     * 显示成功消息
     */
    success(message, duration = APP_CONFIG.DEFAULTS.TOAST_DURATION) {
        this.show(message, 'success', duration);
    },

    /**
     * 显示错误消息
     */
    error(message, duration = APP_CONFIG.DEFAULTS.TOAST_DURATION) {
        this.show(message, 'error', duration);
    },

    /**
     * 显示警告消息
     */
    warning(message, duration = APP_CONFIG.DEFAULTS.TOAST_DURATION) {
        this.show(message, 'warning', duration);
    },

    /**
     * 显示信息消息
     */
    info(message, duration = APP_CONFIG.DEFAULTS.TOAST_DURATION) {
        this.show(message, 'info', duration);
    },

    /**
     * 显示通知
     */
    show(message, type = 'info', duration = APP_CONFIG.DEFAULTS.TOAST_DURATION) {
        // 创建toast容器（如果不存在）
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.className = 'toast-container position-fixed top-0 end-0 p-3';
            container.style.zIndex = '9999';
            document.body.appendChild(container);
        }

        // 创建toast元素
        const toastId = Utils.generateId('toast');
        const iconMap = {
            success: 'fas fa-check-circle text-success',
            error: 'fas fa-exclamation-circle text-danger',
            warning: 'fas fa-exclamation-triangle text-warning',
            info: 'fas fa-info-circle text-info'
        };

        const toastHtml = `
            <div id="${toastId}" class="toast align-items-center border-0" role="alert">
                <div class="d-flex">
                    <div class="toast-body d-flex align-items-center">
                        <i class="${iconMap[type]} me-2"></i>
                        ${message}
                    </div>
                    <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        `;

        container.insertAdjacentHTML('beforeend', toastHtml);
        
        // 初始化并显示toast
        const toastElement = document.getElementById(toastId);
        const bsToast = new bootstrap.Toast(toastElement, {
            autohide: true,
            delay: duration
        });
        
        bsToast.show();

        // 自动移除
        toastElement.addEventListener('hidden.bs.toast', () => {
            toastElement.remove();
        });
    }
};

// 加载状态管理
const Loading = {
    /**
     * 显示全局加载
     */
    show(message = '加载中...') {
        let overlay = document.getElementById('loading-overlay');
        if (!overlay) {
            overlay = document.createElement('div');
            overlay.id = 'loading-overlay';
            overlay.className = 'loading-overlay';
            overlay.innerHTML = `
                <div class="loading-content">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <div class="loading-text mt-3">${message}</div>
                </div>
            `;
            document.body.appendChild(overlay);
        }
        overlay.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    },

    /**
     * 隐藏全局加载
     */
    hide() {
        const overlay = document.getElementById('loading-overlay');
        if (overlay) {
            overlay.style.display = 'none';
            document.body.style.overflow = '';
        }
    },

    /**
     * 显示按钮加载状态
     */
    button(button, loading = true) {
        if (loading) {
            button.disabled = true;
            const originalText = button.innerHTML;
            button.dataset.originalText = originalText;
            button.innerHTML = `
                <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                加载中...
            `;
        } else {
            button.disabled = false;
            button.innerHTML = button.dataset.originalText || button.innerHTML;
        }
    }
};

// 确认对话框
const Confirm = {
    /**
     * 显示确认对话框
     */
    show(options = {}) {
        const defaults = {
            title: '确认操作',
            message: '您确定要执行此操作吗？',
            confirmText: '确认',
            cancelText: '取消',
            type: 'warning',
            onConfirm: () => {},
            onCancel: () => {}
        };
        
        const config = { ...defaults, ...options };
        const modalId = Utils.generateId('confirm-modal');
        
        const iconMap = {
            warning: 'fas fa-exclamation-triangle text-warning',
            danger: 'fas fa-exclamation-circle text-danger',
            info: 'fas fa-info-circle text-info',
            success: 'fas fa-check-circle text-success'
        };

        const modalHtml = `
            <div class="modal fade" id="${modalId}" tabindex="-1">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header border-0">
                            <h5 class="modal-title d-flex align-items-center">
                                <i class="${iconMap[config.type]} me-2"></i>
                                ${config.title}
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            ${config.message}
                        </div>
                        <div class="modal-footer border-0">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                ${config.cancelText}
                            </button>
                            <button type="button" class="btn btn-primary confirm-btn">
                                ${config.confirmText}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);
        
        const modalElement = document.getElementById(modalId);
        const modal = new bootstrap.Modal(modalElement);
        
        // 绑定确认按钮事件
        modalElement.querySelector('.confirm-btn').addEventListener('click', () => {
            config.onConfirm();
            modal.hide();
        });
        
        // 绑定取消事件
        modalElement.addEventListener('hidden.bs.modal', () => {
            modalElement.remove();
        });
        
        modal.show();
    }
};

// AJAX请求封装
const Ajax = {
    /**
     * GET请求
     */
    get(url, options = {}) {
        return this.request(url, { ...options, method: 'GET' });
    },

    /**
     * POST请求
     */
    post(url, data, options = {}) {
        return this.request(url, { ...options, method: 'POST', data });
    },

    /**
     * PUT请求
     */
    put(url, data, options = {}) {
        return this.request(url, { ...options, method: 'PUT', data });
    },

    /**
     * DELETE请求
     */
    delete(url, options = {}) {
        return this.request(url, { ...options, method: 'DELETE' });
    },

    /**
     * 通用请求方法
     */
    async request(url, options = {}) {
        const defaults = {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            },
            showLoading: false,
            showError: true
        };

        const config = { ...defaults, ...options };
        
        if (config.showLoading) {
            Loading.show();
        }

        try {
            const fetchOptions = {
                method: config.method,
                headers: config.headers
            };

            if (config.data) {
                if (config.headers['Content-Type'] === 'application/json') {
                    fetchOptions.body = JSON.stringify(config.data);
                } else {
                    fetchOptions.body = config.data;
                }
            }

            const response = await fetch(url, fetchOptions);
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const result = await response.json();
            
            if (config.showLoading) {
                Loading.hide();
            }

            return result;
        } catch (error) {
            if (config.showLoading) {
                Loading.hide();
            }
            
            if (config.showError) {
                Toast.error('请求失败: ' + error.message);
            }
            
            throw error;
        }
    }
};

// 表单验证
const Validator = {
    /**
     * 验证规则
     */
    rules: {
        required: (value) => value !== null && value !== undefined && value.toString().trim() !== '',
        email: (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value),
        phone: (value) => /^1[3-9]\d{9}$/.test(value),
        number: (value) => !isNaN(value) && isFinite(value),
        min: (value, min) => parseFloat(value) >= min,
        max: (value, max) => parseFloat(value) <= max,
        minLength: (value, length) => value.toString().length >= length,
        maxLength: (value, length) => value.toString().length <= length
    },

    /**
     * 验证表单
     */
    validate(form, rules) {
        const errors = {};
        
        for (const [field, fieldRules] of Object.entries(rules)) {
            const element = form.querySelector(`[name="${field}"]`);
            if (!element) continue;
            
            const value = element.value;
            
            for (const rule of fieldRules) {
                const [ruleName, ruleValue, message] = Array.isArray(rule) ? rule : [rule, null, ''];
                
                if (!this.rules[ruleName]) continue;
                
                const isValid = ruleValue !== null 
                    ? this.rules[ruleName](value, ruleValue)
                    : this.rules[ruleName](value);
                
                if (!isValid) {
                    errors[field] = message || `${field} 验证失败`;
                    break;
                }
            }
        }
        
        return {
            isValid: Object.keys(errors).length === 0,
            errors
        };
    },

    /**
     * 显示验证错误
     */
    showErrors(form, errors) {
        // 清除之前的错误
        form.querySelectorAll('.is-invalid').forEach(el => {
            el.classList.remove('is-invalid');
        });
        form.querySelectorAll('.invalid-feedback').forEach(el => {
            el.remove();
        });
        
        // 显示新错误
        for (const [field, message] of Object.entries(errors)) {
            const element = form.querySelector(`[name="${field}"]`);
            if (element) {
                element.classList.add('is-invalid');
                const feedback = document.createElement('div');
                feedback.className = 'invalid-feedback';
                feedback.textContent = message;
                element.parentNode.appendChild(feedback);
            }
        }
    }
};

// 初始化函数
function initCommonFeatures() {
    // 添加全局样式
    if (!document.getElementById('common-styles')) {
        const style = document.createElement('style');
        style.id = 'common-styles';
        style.textContent = `
            .loading-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.5);
                display: none;
                justify-content: center;
                align-items: center;
                z-index: 10000;
            }
            .loading-content {
                text-align: center;
                color: white;
            }
            .loading-text {
                font-size: 1.1rem;
            }
            .toast-container {
                z-index: 9999;
            }
        `;
        document.head.appendChild(style);
    }

    // 全局错误处理
    window.addEventListener('error', (event) => {
        console.error('Global error:', event.error);
        Toast.error('系统发生错误，请刷新页面重试');
    });

    // 全局未处理的Promise拒绝
    window.addEventListener('unhandledrejection', (event) => {
        console.error('Unhandled promise rejection:', event.reason);
        Toast.error('请求处理失败，请重试');
    });

    // 响应式处理
    const handleResize = Utils.throttle(() => {
        document.body.classList.toggle('mobile', Utils.isMobile());
    }, 250);
    
    window.addEventListener('resize', handleResize);
    handleResize(); // 初始调用

    // 表单增强
    document.addEventListener('submit', (event) => {
        const form = event.target;
        if (form.dataset.validate) {
            // 这里可以添加通用表单验证逻辑
        }
    });
}

// DOM加载完成后初始化
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initCommonFeatures);
} else {
    initCommonFeatures();
}

// 导出到全局
window.Utils = Utils;
window.Toast = Toast;
window.Loading = Loading;
window.Confirm = Confirm;
window.Ajax = Ajax;
window.Validator = Validator;