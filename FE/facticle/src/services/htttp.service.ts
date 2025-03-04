import Axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL;
Axios.defaults.baseURL = API_URL;
Axios.defaults.withCredentials = true;

export class HttpService {
    _axios = Axios.create(); // Axios 인스턴스 생성
   
    addJWTToken(token: string) {
        this._axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }

    removeJWTToken() {
        delete this._axios.defaults.headers.common['Authorization'];
    }

    // 요청 인터셉터 추가 (인터셉트 )
    addRequestInterceptor(onFulfilled: any, onRejected: any) {
        this._axios.interceptors.request.use(onFulfilled, onRejected);
    }

    // 응답 인터셉터 추가
    addResponseInterceptor(onFulfilled: any, onRejected: any) {
        this._axios.interceptors.response.use(onFulfilled, onRejected);
    };

    // 요청 옵션 설정
    getOptionsConfig = (method: string, url: string, data: any) => {
        url = '/api' + url;
        return {
            method,
            url,
            data,
            headers: {
                'Content-Type': 'application/json',
            },
        };
    };

    request(options: any) {
        return new Promise((resolve, reject) => {
            this._axios
                .request(options)
                .then((response) => resolve(response.data))
                .catch((error) => reject(error));
        });
    }

    // GET 요청
    get = async (url: string) => await this.request(this.getOptionsConfig('GET', url, null));

    // POST 요청
    post = async (url: string, data: any = null) => await this.request(this.getOptionsConfig('POST', url, data));

    // PUT 요청
    put = async (url: string, data: any) => await this.request(this.getOptionsConfig('PUT', url, data));

    // PATCH 요청
    patch = async (url: string, data: any) => await this.request(this.getOptionsConfig('PATCH', url, data));

    // DELETE 요청
    delete = async (url: string) => await this.request(this.getOptionsConfig('DELETE', url, null));

    // 파일 업로드
    upload = async (url: string, file: File) => {
        const formData = new FormData();
        formData.append('profileImage', file);

        return await this.request({
            method: 'POST',
            url: '/api' + url,
            data: formData,
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    };
}

export default new HttpService();