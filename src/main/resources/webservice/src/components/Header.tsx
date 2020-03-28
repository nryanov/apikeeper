import React from "react"
import NewEntityModal from "./NewEntityModal";

const Header: React.FunctionComponent = () => (
    <>
        <NewEntityModal/>
        <nav className="navbar navbar-expand-lg navbar-light bg-light">
            <span className="navbar-brand mb-0 h1">Apikeeper</span>

            <div className="collapse navbar-collapse" id="navbarNav">
                <div className="collapse navbar-collapse" id="navbarNavAltMarkup">
                    <form className="form-inline">
                        <button className="btn btn-outline-success" id="newSubjectModalBtn" type="button" data-toggle="modal"
                                data-target="#newSubjectModal">New entity
                        </button>
                    </form>
                </div>
            </div>
        </nav>
    </>
);

export default Header;