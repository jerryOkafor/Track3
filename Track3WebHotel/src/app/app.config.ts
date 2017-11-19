import { InjectionToken } from '@angular/core';
import { environment } from '../environments/environment';

/**
 * Created by m0rpheus on 21/05/2017.
 */
export let APP_CONFIG = new InjectionToken('appConfig');


export let appConfig = {
    endpoint: '/',
    login: '/login/',


};
