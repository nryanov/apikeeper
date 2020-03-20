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
            return {
                ...state,
                entityProps: _.uniqBy([...state.entityProps, action.entityProps], (props) => props.id)
            };
        case Type.CREATE_ENTITY:
            return {
                ...state,
                entityProps: [...state.entityProps, action.entityProps]
            };
        case Type.CREATE_RELATION:
            let branchDef = action.branchDef;
            let relationInfo = action.relation;

            let newEntityStates = {...state.entityStates};
            let leftEntity = newEntityStates[branchDef.left.id];
            let rightEntity = newEntityStates[branchDef.right.id];

            if (relationInfo.relationType.type === 'In') {
                
            } else {

            }

            return {
                ...state,
                entityStates: newEntityStates
            };
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