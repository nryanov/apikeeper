import {createStore, applyMiddleware, Store} from "redux";
import thunk from "redux-thunk";
import { reducer } from "./reducer"
import {KeeperActions, State} from "./types";
import {apiCalls} from "./action";

let initialState: State = {
    entityProps: {},
    entityStates: {},
    selectedEntity: null,
    page: 1,
    maxPage: 1,
    filterByName: null,
    filterByType: null,
    filteredEntityProps: {}
};

// tsconfig: "strictFunctionTypes": false
const storeFactory = (state: State): Store<State, KeeperActions> => applyMiddleware(thunk)(createStore)(
    reducer,
    state
);

const store = storeFactory(initialState);
// @ts-ignore
store.dispatch(apiCalls.findAllEntities());

export default store;