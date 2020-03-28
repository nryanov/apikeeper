import {reducer as updateState} from "../../logic/reducer"
import * as Types from "../../logic/types"

describe("reducer specs", () => {
    it("add new entities to the state after call FIND_ENTITIES", () => {
        let initialState: Types.State = {
            entityProps: {},
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "FIND_ENTITIES",
            entityProps: [
                {
                    id: "1",
                    entityType: "Service",
                    name: "entity",
                    description: null
                }
            ]
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("add new entity to the state after call FIND_ENTITY", () => {
        let initialState: Types.State = {
            entityProps: {},
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "FIND_ENTITY",
            entityProps: {
                id: "1",
                entityType: "Service",
                name: "entity",
                description: null
            }
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("add new entity to the state after call CREATE_ENTITY", () => {
        let initialState: Types.State = {
            entityProps: {},
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "CREATE_ENTITY",
            entityProps: {
                id: "1",
                entityType: "Service",
                name: "entity",
                description: null
            }
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("update entity after call UPDATE_ENTITY", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "updatedName",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "UPDATE_ENTITY",
            entityProps: {
                id: "1",
                entityType: "Service",
                name: "updatedName",
                description: null
            }
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("update entities states after call CREATE_RELATION - Downstream", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {
                "1": [{
                    relation: {
                        id: "3",
                        relationType: "Downstream"
                    },
                    targetEntity: "2"
                }],
                "2": [{
                    relation: {
                        id: "3",
                        relationType: "Upstream"
                    },
                    targetEntity: "1"
                }]
            },
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "CREATE_RELATION",
            branchDef: {
                left: "1",
                relation: {
                    relationType: "Downstream"
                },
                right: "2"
            },
            relation: {
                id: "3",
                relationType: "Downstream"
            }
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("update entities states after call CREATE_RELATION - Upstream", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {
                "1": [{
                    relation: {
                        id: "3",
                        relationType: "Upstream"
                    },
                    targetEntity: "2"
                }],
                "2": [{
                    relation: {
                        id: "3",
                        relationType: "Downstream"
                    },
                    targetEntity: "1"
                }]
            },
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "CREATE_RELATION",
            branchDef: {
                left: "1",
                relation: {
                    relationType: "Upstream"
                },
                right: "2"
            },
            relation: {
                id: "3",
                relationType: "Upstream"
            }
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("update entities states after call FIND_CLOSEST_ENTITY_RELATIONS", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {
                "1": [{
                    relation: {
                        id: "3",
                        relationType: "Downstream"
                    },
                    targetEntity: "2"
                }]
            },
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "FIND_CLOSEST_ENTITY_RELATIONS",
            entityId: "1",
            relations: [{
                id: "1",
                relation: {
                    id: "3",
                    relationType: "Downstream"
                },
                entity: {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            }],
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("update entities states after call FIND_CLOSEST_ENTITY_RELATIONS - some unknown entities were returned", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {
                "1": [{
                    relation: {
                        id: "3",
                        relationType: "Downstream"
                    },
                    targetEntity: "2"
                }]
            },
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "FIND_CLOSEST_ENTITY_RELATIONS",
            entityId: "1",
            relations: [{
                id: "1",
                relation: {
                    id: "3",
                    relationType: "Downstream"
                },
                entity: {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            }],
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("remove entity after call REMOVE_ENTITY", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {
                "1": [{
                    relation: {
                        id: "3",
                        relationType: "Downstream"
                    },
                    targetEntity: "2"
                }],
                "2": [{
                    relation: {
                        id: "3",
                        relationType: "Upstream"
                    },
                    targetEntity: "1"
                }]
            },
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                }
            },
            entityStates: {
                "1": []
            },
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "REMOVE_ENTITY",
            entityId: "2",
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("remove relation after call REMOVE_RELATION", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {
                "1": [{
                    relation: {
                        id: "3",
                        relationType: "Downstream"
                    },
                    targetEntity: "2"
                }],
                "2": [{
                    relation: {
                        id: "3",
                        relationType: "Upstream"
                    },
                    targetEntity: "1"
                }]
            },
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {
                "1": [],
                "2": []
            },
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "REMOVE_RELATION",
            relationId: "3"
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("remove relations after call REMOVE_RELATIONS", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {
                "1": [{
                    relation: {
                        id: "3",
                        relationType: "Downstream"
                    },
                    targetEntity: "2"
                }, {
                        relation: {
                            id: "4",
                            relationType: "Downstream"
                        },
                        targetEntity: "2"
                    }],
                "2": [{
                    relation: {
                        id: "3",
                        relationType: "Upstream"
                    },
                    targetEntity: "1"
                }, {
                    relation: {
                        id: "4",
                        relationType: "Upstream"
                    },
                    targetEntity: "1"
                }]
            },
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {
                "1": [],
                "2": []
            },
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "REMOVE_RELATIONS",
            relationIds: ["3", "4"]
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("remove all entity's relations after call REMOVE_ALL_ENTITY_RELATIONS", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {
                "1": [{
                    relation: {
                        id: "3",
                        relationType: "Downstream"
                    },
                    targetEntity: "2"
                }, {
                    relation: {
                        id: "4",
                        relationType: "Downstream"
                    },
                    targetEntity: "2"
                }],
                "2": [{
                    relation: {
                        id: "3",
                        relationType: "Upstream"
                    },
                    targetEntity: "1"
                }, {
                    relation: {
                        id: "4",
                        relationType: "Upstream"
                    },
                    targetEntity: "1"
                }]
            },
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {
                "1": [],
                "2": []
            },
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "REMOVE_ALL_ENTITY_RELATIONS",
            entityId: "1"
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("select entity by id", () => {
        let initialState: Types.State = {
            entityProps: {},
            entityStates: {},
            selectedEntity: "1",
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {},
            entityStates: {},
            selectedEntity: "2",
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {type: "SELECT_ENTITY", entityId: "2"};

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("change page after call CHANGE_PAGE", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 5,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Service",
                    name: "entity2",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 2,
            maxPage: 5,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "CHANGE_PAGE",
            page: 2
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("update filters after call FILTER_ENTITIES - name pattern and entity type", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Storage",
                    name: "entity2",
                    description: null
                },
                "3": {
                    id: "3",
                    entityType: "MessageQueue",
                    name: "entity3",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Storage",
                    name: "entity2",
                    description: null
                },
                "3": {
                    id: "3",
                    entityType: "MessageQueue",
                    name: "entity3",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: "entity",
            filterByType: "Storage",
            filteredEntityProps: {
                "2": {
                    id: "2",
                    entityType: "Storage",
                    name: "entity2",
                    description: null
                }
            },
        };

        let action: Types.KeeperActions = {
            type: "FILTER_ENTITIES",
            namePattern: "entity",
            entityType: "Storage"
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("update filters after call FILTER_ENTITIES - name pattern", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Storage",
                    name: "entity2",
                    description: null
                },
                "3": {
                    id: "3",
                    entityType: "MessageQueue",
                    name: "entity3",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Storage",
                    name: "entity2",
                    description: null
                },
                "3": {
                    id: "3",
                    entityType: "MessageQueue",
                    name: "entity3",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: "entity",
            filterByType: null,
            filteredEntityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Storage",
                    name: "entity2",
                    description: null
                },
                "3": {
                    id: "3",
                    entityType: "MessageQueue",
                    name: "entity3",
                    description: null
                }
            },
        };

        let action: Types.KeeperActions = {
            type: "FILTER_ENTITIES",
            namePattern: "entity",
            entityType: null
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("update filters after call FILTER_ENTITIES - entity type", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Storage",
                    name: "entity2",
                    description: null
                },
                "3": {
                    id: "3",
                    entityType: "MessageQueue",
                    name: "entity3",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Storage",
                    name: "entity2",
                    description: null
                },
                "3": {
                    id: "3",
                    entityType: "MessageQueue",
                    name: "entity3",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: "Service",
            filteredEntityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                }
            },
        };

        let action: Types.KeeperActions = {
            type: "FILTER_ENTITIES",
            namePattern: null,
            entityType: "Service"
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });

    it("update filters after call FILTER_ENTITIES - clear filters", () => {
        let initialState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Storage",
                    name: "entity2",
                    description: null
                },
                "3": {
                    id: "3",
                    entityType: "MessageQueue",
                    name: "entity3",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: "Service",
            filteredEntityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                }
            },
        };

        let newState: Types.State = {
            entityProps: {
                "1": {
                    id: "1",
                    entityType: "Service",
                    name: "entity1",
                    description: null
                },
                "2": {
                    id: "2",
                    entityType: "Storage",
                    name: "entity2",
                    description: null
                },
                "3": {
                    id: "3",
                    entityType: "MessageQueue",
                    name: "entity3",
                    description: null
                }
            },
            entityStates: {},
            selectedEntity: null,
            page: 1,
            maxPage: 1,
            filterByName: null,
            filterByType: null,
            filteredEntityProps: {},
        };

        let action: Types.KeeperActions = {
            type: "FILTER_ENTITIES",
            namePattern: null,
            entityType: null
        };

        expect(updateState(initialState, action)).toEqual(newState);
        expect(initialState).not.toEqual(newState)
    });
});