import React from "react";
import Header from "./Header";
import EntityList from "./EntityList";
import SelectedEntity from "./SelectedEntity";
import Pagination from "./Pagination";
import Search from "./Search";

const Application: React.FunctionComponent = () => {
    return (
        <>
            <Header/>
            <div className="container-fluid">
                <div className="row mt-4">
                    <div className="col-4">
                        <Search/>
                        <EntityList/>
                        <Pagination/>
                    </div>
                    <div className="col-8">
                        <SelectedEntity/>
                    </div>
                </div>
            </div>
        </>
    )
};

export default Application;