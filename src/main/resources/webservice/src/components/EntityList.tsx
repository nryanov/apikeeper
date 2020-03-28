import React from "react"
import { useSelector } from "react-redux"
import {EntityProps, MAX_PAGE_SIZE, State} from "../logic/types";
import _ from "lodash"
import EntityListItem from "./EntityListItem";


const EntityList: React.FunctionComponent = () => {
    const entityProps = useSelector<State, EntityProps[]>(state => {
        const entityProps = state.filterByName !== null || state.filterByType !== null ? state.filteredEntityProps : state.entityProps;
        return _.slice(Object.values(entityProps), (state.page - 1) * MAX_PAGE_SIZE, (state.page) * MAX_PAGE_SIZE);
    });

    return (
        <table className="table table-hover">
            <thead>
            <tr>
                <th scope="col">Entity list</th>
            </tr>
            </thead>
            <tbody>
                {_.map(entityProps, prop => <EntityListItem key={prop.id} {...prop}/>)}
            </tbody>
        </table>
    )
};

export default EntityList;