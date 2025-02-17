import HttpService from "../htttp.service";

class AuthService {
    login = async (payload : { username: string, password: string }) => {
        const loginEndpoint = '/users/login';
        return await HttpService.post(loginEndpoint, payload);
    }

    loginSocial = async (payload: { provider: string, code: string }) => {
        const loginOauthEndpoint = '/users/login/social';
        return await HttpService.post(loginOauthEndpoint, payload);
    }

    idcheck = async (payload: { username: string }) => {
        const idcheckEndpoint = '/users/check-username';
        return await HttpService.post(idcheckEndpoint, payload);
    }

    nicknamecheck = async (payload: { nickname: string }) => {
        const nicknamecheckEndpoint = '/users/check-nickname';
        return await HttpService.post(nicknamecheckEndpoint, payload);
    }

    register = async(payload: { username: string, password: string, nickname: string }) => {
        const registerEndpoint = '/users/signup';
        return await HttpService.post(registerEndpoint, payload);
    }

    registerSocial = async(payload: { nickname: string }) => {
        const registerOauthEndpoint = '/users/signup/social';
        return await HttpService.post(registerOauthEndpoint, payload);
    }
}

export default new AuthService();
