import {KeeperActions, State} from "./types";
import * as Type from "./actionType";
import _ from 'lodash'
import {Reducer} from "redux";

export const reducer: Reducer<State, KeeperActions> = (state: State, action: KeeperActions): State => {
    switch (action.type) {
        case Type.FIND_ENTITIES:
            return {
                ...state,
                entityProps: {...state.entityProps, ..._.keyBy(action.entityProps, props => props.id.id)}
            };
        case Type.FIND_ENTITY:
            return {
                ...state,
                entityProps: _.set({...state.entityProps}, action.entityProps.id.id, action.entityProps)
            };
        case Type.CREATE_ENTITY:
            return {
                ...state,
                entityProps: _.set({...state.entityProps}, action.entityProps.id.id, action.entityProps)
            };
        case Type.UPDATE_ENTITY:
            return {
                ...state,
                entityProps: _.set({...state.entityProps}, action.entityProps.id.id, action.entityProps)
            };
        case Type.CREATE_RELATION: return state;
        case Type.FIND_CLOSEST_ENTITY_RELATIONS:
            return {
                ...state,
                entityStates: _.set({...state.entityStates}, action.entityId.id, action.relations)
            };
        case Type.REMOVE_ENTITY:
            return {
                ...state,
                entityProps: _.pickBy({...state.entityProps}, (value, key) => key !== action.entityId.id),
                entityStates: _.pickBy({...state.entityStates}, (value, key) => key !== action.entityId.id),
            };
        case Type.REMOVE_RELATION:
            return {
                ...state,
                entityStates: _.mapValues({...state.entityStates}, leafs => _.filter(leafs, leaf => leaf.relation.id !== action.relationId))
            };
        case Type.REMOVE_RELATIONS:
            return {
                ...state,
                entityStates: _.mapValues({...state.entityStates}, leafs => _.filter(leafs, leaf => !action.relationIds.includes(leaf.relation.id)))
            };
        case Type.REMOVE_ALL_ENTITY_RELATIONS:
            return {
                ...state,
                entityStates: _.set({...state.entityStates}, action.entityId.id, [])
            };
        default: return state;
    }
};