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
    public limit: number = 0;
    public order: string;
    public currentProgress: string;
    public visiblePercentage: string = "100%";
    constructor(private todoListService: TodoListService) {
        // this.users = this.userListService.getUsers();
    }

    ngOnInit(): void {
        this.todoListService.getTodos(this.searchOwner, this.searchCategory,this.limit, this.order).subscribe(
            todos => this.todos = todos,
            err => {
                console.log(err);
            }
        );
    }

    onChange(): void {
        this.todoListService.getTodos(this.searchOwner, this.searchCategory,this.limit, this.order).subscribe(
            todos => this.todos = todos,
            err => {
                console.log(err);
            }
        );
    }

    changeProgress(): void {
        var pBar1 : Element = document.getElementById("pBar1");

        let number :number = +this.currentProgress;

        pBar1.setAttribute("style", "width: " + number + "%");
        this.visiblePercentage = this.currentProgress + "%";
    }
}