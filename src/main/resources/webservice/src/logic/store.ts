import {createStore, applyMiddleware, Store} from "redux";
import thunk from "redux-thunk";
import { reducer } from "./reducer"
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
            type: "Storage",
            name: "entity2",
            description: null
        },
        "3": {
            id: "3",
            type: "MessageQueue",
            name: "entity3",
            description: null
        },
        "4": {
            id: "4",
            type: "MessageQueue",
            name: "entity4",
            description: null
        },
        "5": {
            id: "5",
            type: "MessageQueue",
            name: "entity5",
            description: null
        },
        "6": {
            id: "6",
            type: "MessageQueue",
            name: "entity6",
            description: null
        },
        "7": {
            id: "7",
            type: "MessageQueue",
            name: "entity7",
            description: null
        }
    },
    entityStates: {
        "1": [{
            relation: {
                id: "1",
                relationType: "Downstream"
            },
            targetEntity: "2"
        }],
        "2": [{
            relation: {
                id: "1",
                relationType: "Upstream"
            },
            targetEntity: "1"
        }, {
            relation: {
                id: "2",
                relationType: "Downstream"
            },
            targetEntity: "3"
        }],
        "3": [{
            relation: {
                id: "2",
                relationType: "Upstream"
            },
            targetEntity: "2"
        }]
    },
    selectedEntity: null,
    page: 1,
    maxPage: 2,
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

export default store;