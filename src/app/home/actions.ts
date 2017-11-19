import { Action } from '@ngrx/store';

export const START_LOADING = '[Home] Loading';
export const STOP_LOADING = '[Home] Not Loading';
export const APP_TITLE = '[Home] App Title';
export const LOCATION_CODE = '[Home] Location Code';
export const SET_LOCATION = '[Home] Set Location';
export const Load_LOCATION = '[Home] Load Location';

export class StartLoading implements Action {
  readonly type = START_LOADING;
}

export class StopLoading implements Action {
  readonly type = STOP_LOADING;
}

export class ShowTitle implements Action {
  readonly type = APP_TITLE;
}

export class GetLocationCode implements Action {
  readonly type = LOCATION_CODE;
  constructor(public payload?: string) {}
}

export class SetLocationCode implements Action {
  readonly type = SET_LOCATION;
  constructor(public payload: any) {}
}



export type Actions = StartLoading | StopLoading | ShowTitle | GetLocationCode | SetLocationCode;
