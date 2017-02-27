import { Component, OnInit } from '@angular/core';
import { TodoListService } from "./todo-list.service";
import { Todo } from "./todo";
import {FilterBy} from "../users/filter.pipe";

@Component({
    selector: 'todo-list-component',
    templateUrl: 'todo-list.component.html',
    providers: [ FilterBy ]
})

export class TodoListComponent implements OnInit {
    public todos: Todo[];
    public searchOwner: string;
    public searchCategory: string;
    public complete: string;
    public limit: number = 0;
    public order: string;
    constructor(private todoListService: TodoListService) {
        // this.users = this.userListService.getUsers();
    }

    ngOnInit(): void {
        this.todoListService.getTodos(this.searchOwner, this.searchCategory, this.complete, this.limit, this.order).subscribe(
            todos => this.todos = todos,
            err => {
                console.log(err);
            }
        );
    }

    onChange(): void {
        this.todoListService.getTodos(this.searchOwner, this.searchCategory, this.complete, this.limit, this.order).subscribe(
            todos => this.todos = todos,
            err => {
                console.log(err);
            }
        );
    }
}