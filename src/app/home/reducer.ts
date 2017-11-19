import * as home from './actions';

export interface State {
  showLoading: boolean;
  Location: any;
}

const initialState: State = {
  showLoading: false,
  Location: {},
};

export function reducer(state = initialState, action: home.Actions): State {
  switch (action.type) {
    case home.STOP_LOADING:
      return {
        showLoading: false,
        Location: state.Location,
      };

    case home.START_LOADING:
      return {
        showLoading: true,
        Location: state.Location
      };
    case home.LOCATION_CODE: {
    const query = action.payload;
    return state
    }
    case home.SET_LOCATION: {
    return {
        Location: Object.assign({}, action.payload.results[0].geometry.location),
        showLoading: state.showLoading
    }
    }
    default:
      return state;
  }
}

export const getShowLoading = (state: State) => state.showLoading;
export const getLocationCode = (state: State) => state.Location;
