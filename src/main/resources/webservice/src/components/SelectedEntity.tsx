import React from "react"
import {EntityProps, State} from "../logic/types";
import { useSelector } from "react-redux"
import EntityInfo from "./EntityInfo";
import EntityVisualization from "./EntityVisualization";


const SelectedEntity: React.FunctionComponent = () => {
    const selected = useSelector<State, EntityProps | null>(state => {
        let id = state.selectedEntity;

        return id !== null ? state.entityProps[id] : null
    });

    if (selected === null) {
        return <></>
    } else {
        return (
            <div className="card">
                <div className="card-header">
                    {selected?.name}
                </div>
                <div className="card-body">
                    <ul className="nav nav-tabs" id="entityTab" role="tablist">
                        <li className="nav-item">
                            <a className="nav-link active" id="info-tab" data-toggle="tab" href="#info" role="tab"
                               aria-controls="info" aria-selected="true">Info</a>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link" id="visualization-tab" data-toggle="tab" href="#visualization" role="tab"
                               aria-controls="visualization" aria-selected="false">Visualization</a>
                        </li>
                    </ul>
                    <div className="tab-content" id="entityTabContent">
                        <div className="tab-pane fade show active" id="info" role="tabpanel" aria-labelledby="home-tab">
                            <EntityInfo {...selected}/>
                        </div>
                        <div className="tab-pane fade" id="visualization" role="tabpanel" aria-labelledby="profile-tab">
                            <EntityVisualization {...selected}/>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
};

export default SelectedEntity;
