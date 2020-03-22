import {createStore, applyMiddleware, Store, Middleware} from "redux";
import thunk from 'redux-thunk';
import { reducer } from './reducer'
import {KeeperActions, State} from "./types";

// let initialState: State = {
//     entityProps: {},
//     entityStates: {},
//     selectedEntity: null
// };

let initialState: State = {
    entityProps: {
        "1": {
            id: "1",
            type: "Service",
            name: "entity1",
            description: null
        },
        "2": {
            id: "2",
            type: "Service",
            name: "entity2",
            description: null
        }
    },
    entityStates: {},
    selectedEntity: null
};

// tsconfig: "strictFunctionTypes": false
const storeFactory = (state: State): Store<State, KeeperActions> => applyMiddleware(thunk)(createStore)(
    reducer,
    state
);

const store = storeFactory(initialState);

export default store;