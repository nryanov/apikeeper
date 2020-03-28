import axios, {AxiosRequestConfig} from "axios"
import {BranchDef, EntityDef, EntityProps, Id, LeafFull, RelationProps} from "./types";

const apiBase = "";
const apiVersion = "v1";
const apiPath = `${apiBase}/${apiVersion}`;

function request<T = any>(config: AxiosRequestConfig) {
    return axios.request<T>(config).then(response => response.data);
}

export const findAllEntities = () => {
    return request<EntityProps[]>({
        url: `${apiPath}/entity`,
        method: "GET",
    });
};

export const findEntities = (page: number, entries: number) => {
    return request<EntityProps[]>({
        url: `${apiPath}/entity?page=${page}&entries=${entries}`,
        method: "GET",
    });
};

export const findEntitiesByName = (pattern: string, entries: number) => {
    return request<EntityProps[]>({
        url: `${apiPath}/entity/filter?name=${pattern}&entries=${entries}`,
        method: "GET",
    });
};

export const findEntity = (id: Id) => {
    return request<EntityProps>({
        url: `${apiPath}/entity/${id}`,
        method: "GET",
    });
};

export const findClosestEntityRelations = (id: Id) => {
    return request<LeafFull[]>({
        url: `${apiPath}/entity/${id}/relation`,
        method: "GET",
    });
};

export const createEntity = (entityDef: EntityDef) => {
    return request<EntityProps>({
        url: `${apiPath}/entity`,
        method: "POST",
        data: entityDef
    });
};

export const updateEntity = (entity: EntityProps) => {
    return request<EntityProps>({
        url: `${apiPath}/entity`,
        method: "PUT",
        data: entity
    });
};

export const createRelation = (branchDef: BranchDef) => {
    return request<RelationProps>({
        url: `${apiPath}/relation`,
        method: "POST",
        data: branchDef
    });
};

export const removeEntity = (id: Id) => {
    return request<void>({
        url: `${apiPath}/entity/${id}`,
        method: "DELETE"
    });
};

export const removeRelation = (id: Id) => {
    return request<void>({
        url: `${apiPath}/relation/${id}`,
        method: "DELETE",
    });
};

export const removeRelations = (ids: Id[]) => {
    return request<void>({
        url: `${apiPath}/relation`,
        method: "DELETE",
    });
};

export const removeAllEntityRelations = (id: Id) => {
    return request<void>({
        url: `${apiPath}/entity/${id}/relation`,
        method: "DELETE",
    });
};
