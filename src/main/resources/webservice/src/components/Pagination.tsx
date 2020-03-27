import React from "react"
import { useSelector } from "react-redux"
import {useDispatch} from "react-redux";
import {State} from "../logic/types";
import * as action from "../logic/action";

const Pagination: React.FunctionComponent = () => {
    const {currentPage, maxPage} = useSelector<State, any>(state => ({
        currentPage: state.page,
        maxPage:  state.maxPage
    }));

    const dispatch = useDispatch();

    const changePage = (page: number) => {
        dispatch(action.actionCalls.changePage(page))
    };

    return (
        <nav aria-label="Page navigation example">
            <ul className="pagination">
                <li className={`page-item ${currentPage === 1 ? 'disabled' : ''}`}>
                    <button type="button" className="page-link" onClick={() => changePage(currentPage + 1)}>Previous</button>
                </li>
                <li className={`page-item ${currentPage === maxPage ? 'disabled' : ''}`}>
                    <button type="button" className="page-link" onClick={() => changePage(currentPage - 1)}>Next</button>
                </li>
            </ul>
        </nav>
    )
};

export default Pagination;