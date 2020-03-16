import {createStore, applyMiddleware, Store, Middleware} from "redux";
import thunk from 'redux-thunk';
import { reducer } from './reducer'
import {KeeperActions, State} from "./types";

let initialState: State = {
    entityNames: [],
    entityStates: {}
};

// tsconfig: "strictFunctionTypes": false
const storeFactory = (state: State): Store<State, KeeperActions> => applyMiddleware(thunk)(createStore)(
    reducer,
    state
);

const store = storeFactory(initialState);

export default store;