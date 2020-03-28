import {BranchDef, EntityProps, KeeperActions, Leaf, MAX_PAGE_SIZE, RelationProps, State} from "./types";
import * as Type from "./actionType";
import _ from "lodash"
import {Reducer} from "redux";

export const reducer: Reducer<State, KeeperActions> = (state: State, action: KeeperActions): State => {
    switch (action.type) {
        case Type.FIND_ENTITIES:
            return {
                ...state,
                entityProps: {...state.entityProps, ..._.keyBy(action.entityProps, props => props.id)},
                page: 1,
                maxPage: Math.ceil(Object.keys(action.entityProps).length / MAX_PAGE_SIZE)
            };
        case Type.FIND_ENTITY:
            return {
                ...state,
                entityProps: _.set({...state.entityProps}, action.entityProps.id, action.entityProps)
            };
        case Type.CREATE_ENTITY:
            return {
                ...state,
                entityProps: _.set({...state.entityProps}, action.entityProps.id, action.entityProps)
            };
        case Type.UPDATE_ENTITY:
            return {
                ...state,
                entityProps: _.set({...state.entityProps}, action.entityProps.id, action.entityProps)
            };
        case Type.CREATE_RELATION:
            let relationProps: RelationProps = action.relation;
            let branchDef: BranchDef = action.branchDef;

            let leftRelation: RelationProps;
            let rightRelation: RelationProps;

            if (relationProps.relationType === "Downstream") {
                leftRelation = {
                    relationType: "Downstream",
                    id: relationProps.id
                };
                rightRelation = {
                    relationType: "Upstream",
                    id: relationProps.id
                };
            } else {
                leftRelation = {
                    relationType: "Upstream",
                    id: relationProps.id
                };
                rightRelation = {
                    relationType: "Downstream",
                    id: relationProps.id
                };
            }

            let leftEntityState: Leaf = {
                relation: leftRelation,
                targetEntity: branchDef.right
            };
            let rightEntityState: Leaf = {
                relation: rightRelation,
                targetEntity: branchDef.left
            };

            let oldLeftEntityState: Leaf[] = state.entityStates[branchDef.left] || [];
            let oldRightEntityState: Leaf[] = state.entityStates[branchDef.right] || [];

            let newEntityStates = {...state.entityStates};
            _.set(newEntityStates, branchDef.left, [...oldLeftEntityState, leftEntityState]);
            _.set(newEntityStates, branchDef.right, [...oldRightEntityState, rightEntityState]);

            return {
                ...state,
                entityStates: newEntityStates
            };
        case Type.FIND_CLOSEST_ENTITY_RELATIONS:
            let possibleNewEntities: EntityProps[] = _.map(action.relations, leaf => leaf.entity);
            let closestRelations: Leaf[] = _.map(action.relations, leaf => (
                {
                    relation: leaf.relation,
                    targetEntity: leaf.entity.id
                }
            ));

            return {
                ...state,
                entityProps: {...state.entityProps, ..._.keyBy(possibleNewEntities, props => props.id)},
                entityStates: _.set({...state.entityStates}, action.entityId, closestRelations)
            };
        case Type.REMOVE_ENTITY:
            return {
                ...state,
                entityProps: _.pickBy({...state.entityProps}, (value, key) => key !== action.entityId),
                entityStates: _.mapValues(
                    _.pickBy({...state.entityStates}, (value, key) => key !== action.entityId),
                        leafs => _.filter(leafs, leaf => leaf.targetEntity !== action.entityId)
                ),
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
            let entityStatesAfterRemovingEntityRelations = _.mapValues(
                _.set({...state.entityStates}, action.entityId, []),
                    leafs => _.filter(leafs, leaf => leaf.targetEntity !== action.entityId)
            );

            return {
                ...state,
                entityStates: entityStatesAfterRemovingEntityRelations
            };
        case Type.SELECT_ENTITY:
            return {
                ...state,
                selectedEntity: action.entityId
            };
        case Type.CHANGE_PAGE:
            return {
                ...state,
                page: action.page
            };
        case Type.FILTER_ENTITIES:
            // if at least one filter is applied
            if (action.namePattern || action.entityType) {
                const nameFilter = (entityProp: EntityProps) => action.namePattern !== null ? entityProp.name.includes(action.namePattern) : true;
                const typeFilter = (entityProp: EntityProps) => action.entityType !== null ? entityProp.type === action.entityType : true;

                const filteredEntityProps = _.pickBy(state.entityProps, (value, key) => nameFilter(value) && typeFilter(value));

                return {
                    ...state,
                    page: 1,
                    maxPage: Math.ceil(Object.keys(filteredEntityProps).length / MAX_PAGE_SIZE),
                    filterByName: action.namePattern,
                    filterByType: action.entityType,
                    filteredEntityProps: filteredEntityProps
                };
            } else { // otherwise select all entities
                return {
                    ...state,
                    page: 1,
                    maxPage: Math.ceil(Object.keys(state.entityProps).length / MAX_PAGE_SIZE),
                    filterByName: null,
                    filterByType: null,
                    filteredEntityProps: {}
                }
            }
        default: return state;
    }
};