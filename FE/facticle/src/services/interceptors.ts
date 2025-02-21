import authService from "./auth/auth.service";
import HttpService from "./htttp.service";
import { AxiosRequestConfig, AxiosResponse, AxiosError } from "axios";

export const setupAxiosInterceptors = (onUnauthenticated: () => void): void => {
  const onRequestSuccess = (config : AxiosRequestConfig): AxiosRequestConfig => {
    return config;
  };

  const onRequestFail = (error: AxiosError): Promise<never> => Promise.reject(error);

  HttpService.addRequestInterceptor(onRequestSuccess, onRequestFail);

  const onResponseSuccess = (response: AxiosResponse): AxiosResponse => response;

  const onResponseFail = async (error: AxiosError): Promise<never> => {
    const responseData = error.response?.data as { data?: { is_expired?: boolean } } | undefined;
    if (error.response?.status === 401) {
      console.log('test');
      if (responseData?.data?.is_expired) {
        await authService.renewToken()
          .then((response: any) => {
            if (response?.data?.code === 200) {
              return HttpService.addJWTToken(response.data.access_token);
            }
          })
          .catch(() => {
            onUnauthenticated();
          });
      }
    } else {
      onUnauthenticated();
    }
    return Promise.reject(error);
  }

  HttpService.addResponseInterceptor(onResponseSuccess, onResponseFail);
};
