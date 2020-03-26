import React from "react"

class Search extends React.Component {
    render() {
        return (
            <div className="input-group mb-3">
                <div className="input-group-prepend">
                    <span className="input-group-text" id="basic-addon1">Search</span>
                </div>
                <input type="text" className="form-control" placeholder="Subject name" aria-label="subject"
                       aria-describedby="basic-addon1"/>
            </div>
        );
    }
}

export default Search;