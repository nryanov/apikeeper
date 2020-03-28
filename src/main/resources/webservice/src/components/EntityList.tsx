import React from "react"
import { useSelector } from "react-redux"
import {State} from "../logic/types";
import _ from "lodash"
import EntityListItem from "./EntityListItem";


const EntityList: React.FunctionComponent = () => {
    const entityProps = useSelector<State, any>(state => state.filterByName !== null || state.filterByType !== null ? state.filteredEntityProps : state.entityProps);

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