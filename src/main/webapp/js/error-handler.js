/**
 * 全局错误处理和用户反馈系统
 * 提供统一的错误处理、日志记录和用户友好的错误提示
 */

class ErrorHandler {
    constructor() {
        this.errorQueue = [];
        this.maxQueueSize = 50;
        this.retryAttempts = 3;
        this.retryDelay = 1000;
        
        this.init();
    }

    init() {
        this.setupGlobalErrorHandlers();
        this.setupNetworkErrorHandling();
        this.setupFormValidationErrors();
        this.createErrorReportingUI();
    }

    setupGlobalErrorHandlers() {
        // JavaScript运行时错误
        window.addEventListener('error', (event) => {
            this.handleJavaScriptError({
                message: event.message,
                filename: event.filename,
                lineno: event.lineno,
                colno: event.colno,
                error: event.error,
                type: 'javascript'
            });
        });

        // Promise未捕获的拒绝
        window.addEventListener('unhandledrejection', (event) => {
            this.handlePromiseRejection({
                reason: event.reason,
                promise: event.promise,
                type: 'promise'
            });
        });

        // 资源加载错误
        window.addEventListener('error', (event) => {
            if (event.target !== window) {
                this.handleResourceError({
                    element: event.target,
                    source: event.target.src || event.target.href,
                    type: 'resource'
                });
            }
        }, true);
    }

    setupNetworkErrorHandling() {
        // 拦截fetch请求
        const originalFetch = window.fetch;
        window.fetch = async (...args) => {
            try {
                const response = await originalFetch(...args);
                
                if (!response.ok) {
                    this.handleNetworkError({
                        url: args[0],
                        status: response.status,
                        statusText: response.statusText,
                        type: 'network'
                    });
                }
                
                return response;
            } catch (error) {
                this.handleNetworkError({
                    url: args[0],
                    error: error.message,
                    type: 'network'
                });
                throw error;
            }
        };

        // 拦截XMLHttpRequest
        const originalXHROpen = XMLHttpRequest.prototype.open;
        const originalXHRSend = XMLHttpRequest.prototype.send;
        
        XMLHttpRequest.prototype.open = function(method, url, ...args) {
            this._url = url;
            this._method = method;
            return originalXHROpen.call(this, method, url, ...args);
        };
        
        XMLHttpRequest.prototype.send = function(...args) {
            this.addEventListener('error', () => {
                window.errorHandler.handleNetworkError({
                    url: this._url,
                    method: this._method,
                    status: this.status,
                    type: 'xhr'
                });
            });
            
            this.addEventListener('timeout', () => {
                window.errorHandler.handleNetworkError({
                    url: this._url,
                    method: this._method,
                    error: 'Request timeout',
                    type: 'xhr'
                });
            });
            
            return originalXHRSend.call(this, ...args);
        };
    }

    setupFormValidationErrors() {
        document.addEventListener('invalid', (event) => {
            this.handleValidationError({
                element: event.target,
                message: event.target.validationMessage,
                type: 'validation'
            });
        }, true);
    }

    handleJavaScriptError(errorInfo) {
        console.error('JavaScript Error:', errorInfo);
        
        const userMessage = this.getUserFriendlyMessage(errorInfo);
        this.showErrorToUser(userMessage, 'error');
        
        this.logError(errorInfo);
        this.addToQueue(errorInfo);
    }

    handlePromiseRejection(errorInfo) {
        console.error('Promise Rejection:', errorInfo);
        
        const userMessage = this.getUserFriendlyMessage(errorInfo);
        this.showErrorToUser(userMessage, 'warning');
        
        this.logError(errorInfo);
        this.addToQueue(errorInfo);
    }

    handleResourceError(errorInfo) {
        console.error('Resource Error:', errorInfo);
        
        // 尝试重新加载资源
        this.retryResourceLoad(errorInfo);
        
        this.logError(errorInfo);
        this.addToQueue(errorInfo);
    }

    handleNetworkError(errorInfo) {
        console.error('Network Error:', errorInfo);
        
        const userMessage = this.getNetworkErrorMessage(errorInfo);
        this.showErrorToUser(userMessage, 'error');
        
        this.logError(errorInfo);
        this.addToQueue(errorInfo);
    }

    handleValidationError(errorInfo) {
        console.warn('Validation Error:', errorInfo);
        
        this.showFieldError(errorInfo.element, errorInfo.message);
        this.logError(errorInfo);
    }

    getUserFriendlyMessage(errorInfo) {
        const errorMessages = {
            'TypeError': '数据类型错误，请刷新页面重试',
            'ReferenceError': '系统组件未正确加载，请刷新页面',
            'SyntaxError': '系统配置错误，请联系管理员',
            'RangeError': '数据范围错误，请检查输入',
            'NetworkError': '网络连接异常，请检查网络后重试',
            'TimeoutError': '请求超时，请重试',
            'default': '系统发生未知错误，请刷新页面重试'
        };

        const errorType = errorInfo.error?.name || errorInfo.type || 'default';
        return errorMessages[errorType] || errorMessages.default;
    }

    getNetworkErrorMessage(errorInfo) {
        const statusMessages = {
            400: '请求参数错误',
            401: '未授权访问，请重新登录',
            403: '权限不足，无法访问',
            404: '请求的资源不存在',
            408: '请求超时，请重试',
            429: '请求过于频繁，请稍后重试',
            500: '服务器内部错误',
            502: '网关错误',
            503: '服务暂时不可用',
            504: '网关超时',
            default: '网络请求失败，请检查网络连接'
        };

        return statusMessages[errorInfo.status] || statusMessages.default;
    }

    showErrorToUser(message, type = 'error') {
        // 使用Toast显示错误
        if (window.Toast) {
            window.Toast[type](message);
        } else {
            // 降级到alert
            alert(message);
        }
    }

    showFieldError(element, message) {
        // 移除之前的错误提示
        const existingError = element.parentNode.querySelector('.error-message');
        if (existingError) {
            existingError.remove();
        }

        // 添加错误样式
        element.classList.add('is-invalid');

        // 创建错误提示
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message invalid-feedback';
        errorDiv.textContent = message;
        element.parentNode.appendChild(errorDiv);

        // 自动清除错误（当用户开始输入时）
        const clearError = () => {
            element.classList.remove('is-invalid');
            errorDiv.remove();
            element.removeEventListener('input', clearError);
            element.removeEventListener('change', clearError);
        };

        element.addEventListener('input', clearError);
        element.addEventListener('change', clearError);
    }

    retryResourceLoad(errorInfo) {
        const element = errorInfo.element;
        const originalSrc = errorInfo.source;
        
        if (!element.dataset.retryCount) {
            element.dataset.retryCount = '0';
        }
        
        const retryCount = parseInt(element.dataset.retryCount);
        
        if (retryCount < this.retryAttempts) {
            setTimeout(() => {
                element.dataset.retryCount = (retryCount + 1).toString();
                
                if (element.tagName === 'IMG') {
                    element.src = originalSrc + '?retry=' + Date.now();
                } else if (element.tagName === 'LINK') {
                    element.href = originalSrc + '?retry=' + Date.now();
                } else if (element.tagName === 'SCRIPT') {
                    const newScript = document.createElement('script');
                    newScript.src = originalSrc + '?retry=' + Date.now();
                    element.parentNode.replaceChild(newScript, element);
                }
            }, this.retryDelay * (retryCount + 1));
        } else {
            // 重试失败，显示占位符或错误信息
            this.showResourceLoadFailure(element);
        }
    }

    showResourceLoadFailure(element) {
        if (element.tagName === 'IMG') {
            element.alt = '图片加载失败';
            element.style.cssText = `
                display: inline-block;
                width: 100px;
                height: 100px;
                background: #f8f9fa;
                border: 1px dashed #dee2e6;
                text-align: center;
                line-height: 100px;
                color: #6c757d;
                font-size: 12px;
            `;
        }
    }

    logError(errorInfo) {
        const logEntry = {
            timestamp: new Date().toISOString(),
            userAgent: navigator.userAgent,
            url: window.location.href,
            ...errorInfo
        };

        // 发送到服务器（如果配置了错误收集端点）
        this.sendErrorToServer(logEntry);
        
        // 本地存储（用于离线分析）
        this.storeErrorLocally(logEntry);
    }

    sendErrorToServer(errorInfo) {
        // 这里可以配置错误收集服务
        const errorEndpoint = '/sup/api/errors';
        
        fetch(errorEndpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(errorInfo)
        }).catch(() => {
            // 发送失败时静默处理，避免递归错误
        });
    }

    storeErrorLocally(errorInfo) {
        try {
            const errors = JSON.parse(localStorage.getItem('app_errors') || '[]');
            errors.push(errorInfo);
            
            // 只保留最近的50个错误
            if (errors.length > 50) {
                errors.splice(0, errors.length - 50);
            }
            
            localStorage.setItem('app_errors', JSON.stringify(errors));
        } catch (e) {
            // localStorage可能不可用，静默处理
        }
    }

    addToQueue(errorInfo) {
        this.errorQueue.push(errorInfo);
        
        if (this.errorQueue.length > this.maxQueueSize) {
            this.errorQueue.shift();
        }
    }

    createErrorReportingUI() {
        // 创建错误报告按钮（仅在开发环境显示）
        if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
            const reportButton = document.createElement('button');
            reportButton.id = 'error-report-btn';
            reportButton.innerHTML = '<i class="fas fa-bug"></i>';
            reportButton.className = 'btn btn-sm btn-outline-danger position-fixed';
            reportButton.style.cssText = `
                bottom: 20px;
                right: 20px;
                z-index: 9999;
                border-radius: 50%;
                width: 50px;
                height: 50px;
                display: none;
            `;
            
            reportButton.addEventListener('click', () => {
                this.showErrorReport();
            });
            
            document.body.appendChild(reportButton);
            
            // 当有错误时显示按钮
            if (this.errorQueue.length > 0) {
                reportButton.style.display = 'block';
            }
        }
    }

    showErrorReport() {
        const modal = document.createElement('div');
        modal.className = 'modal fade';
        modal.innerHTML = `
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">错误报告</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">错误数量: ${this.errorQueue.length}</label>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">错误详情:</label>
                            <textarea class="form-control" rows="10" readonly>${JSON.stringify(this.errorQueue, null, 2)}</textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
                        <button type="button" class="btn btn-primary" onclick="errorHandler.exportErrors()">导出错误</button>
                        <button type="button" class="btn btn-warning" onclick="errorHandler.clearErrors()">清除错误</button>
                    </div>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
        
        modal.addEventListener('hidden.bs.modal', () => {
            modal.remove();
        });
    }

    exportErrors() {
        const errorData = {
            timestamp: new Date().toISOString(),
            userAgent: navigator.userAgent,
            url: window.location.href,
            errors: this.errorQueue
        };
        
        const blob = new Blob([JSON.stringify(errorData, null, 2)], {
            type: 'application/json'
        });
        
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `error-report-${Date.now()}.json`;
        a.click();
        
        URL.revokeObjectURL(url);
    }

    clearErrors() {
        this.errorQueue = [];
        localStorage.removeItem('app_errors');
        
        const reportButton = document.getElementById('error-report-btn');
        if (reportButton) {
            reportButton.style.display = 'none';
        }
        
        if (window.Toast) {
            window.Toast.success('错误记录已清除');
        }
    }

    // 公共API
    reportError(error, context = {}) {
        this.handleJavaScriptError({
            message: error.message || error,
            error: error,
            context: context,
            type: 'manual'
        });
    }

    getErrorStats() {
        return {
            totalErrors: this.errorQueue.length,
            errorTypes: this.errorQueue.reduce((acc, error) => {
                acc[error.type] = (acc[error.type] || 0) + 1;
                return acc;
            }, {}),
            recentErrors: this.errorQueue.slice(-5)
        };
    }
}

// 网络状态监控
class NetworkMonitor {
    constructor() {
        this.isOnline = navigator.onLine;
        this.init();
    }

    init() {
        window.addEventListener('online', () => {
            this.isOnline = true;
            this.handleOnline();
        });

        window.addEventListener('offline', () => {
            this.isOnline = false;
            this.handleOffline();
        });

        // 定期检查网络状态
        setInterval(() => {
            this.checkNetworkStatus();
        }, 30000);
    }

    handleOnline() {
        if (window.Toast) {
            window.Toast.success('网络连接已恢复');
        }
        
        document.body.classList.remove('offline');
        
        // 重试失败的请求
        this.retryFailedRequests();
    }

    handleOffline() {
        if (window.Toast) {
            window.Toast.warning('网络连接已断开，请检查网络设置');
        }
        
        document.body.classList.add('offline');
    }

    async checkNetworkStatus() {
        try {
            const response = await fetch('/sup/api/ping', {
                method: 'HEAD',
                cache: 'no-cache'
            });
            
            if (!response.ok && this.isOnline) {
                this.handleOffline();
            }
        } catch (error) {
            if (this.isOnline) {
                this.handleOffline();
            }
        }
    }

    retryFailedRequests() {
        // 这里可以实现重试逻辑
        console.log('Retrying failed requests...');
    }
}

// 初始化
document.addEventListener('DOMContentLoaded', () => {
    window.errorHandler = new ErrorHandler();
    window.networkMonitor = new NetworkMonitor();
});

// 导出到全局
window.ErrorHandler = ErrorHandler;
window.NetworkMonitor = NetworkMonitor;