import {KeeperActions, State} from "./types";
import * as Type from "./actionType";
import _ from 'lodash'
import {Reducer} from "redux";

export const reducer: Reducer<State, KeeperActions> = (state: State, action: KeeperActions): State => {
    switch (action.type) {
        case Type.FIND_ENTITIES:
            return {
                ...state,
                entityProps: _.uniqBy([...state.entityProps, ...action.entityProps], (props) => props.id)
            };
        case Type.FIND_ENTITY:
            return state;
        case Type.CREATE_ENTITY:
            return state;
        case Type.CREATE_RELATION:
            return state;
        case Type.FIND_CLOSEST_ENTITY_RELATIONS:
            return state;
        case Type.REMOVE_ENTITY:
            return state;
        case Type.REMOVE_RELATION:
            return state;
        case Type.REMOVE_RELATIONS:
            return state;
        case Type.REMOVE_ALL_ENTITY_RELATIONS:
            return state;
        default: return state;
    }
};