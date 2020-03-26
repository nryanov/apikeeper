import React from "react"
import {EntityProps} from "../logic/types";


const EntityInfo: React.FunctionComponent<EntityProps> = (props) => {
    return (
        <div className="card mt-4" style={{
            width: "18rem"
        }}>
            <div className="card-body">
                <h5 className="card-title">{props.name}</h5>
                <h6 className="card-subtitle mb-2 text-muted">{props.type}</h6>
                {props.description ?
                    <p className="card-text">{props.description}</p> : <></>
                }
            </div>
        </div>
    )
};

export default EntityInfo;