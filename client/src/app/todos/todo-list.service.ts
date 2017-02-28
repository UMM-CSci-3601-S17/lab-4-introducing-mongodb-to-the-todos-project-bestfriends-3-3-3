import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Todo } from './todo';
import { Observable } from "rxjs";

@Injectable()
export class TodoListService {
    private todoUrl: string = API_URL + "todos";
    constructor(private http:Http) { }

    getTodos(owner: string, category: string, limit : number, order : string): Observable<Todo[]> {
        let tempUrl : string = this.urlBuilder(owner,category,limit, order);
        console.log(tempUrl);
        return this.http.request(tempUrl).map(res => res.json());
    }

    urlBuilder(owner: string, category: string, limit : number, order : string): string{
        let addUrl : string = this.todoUrl;
        let ownerFirst: boolean = false;
        let catFirst: boolean = false;
        let limFirst: boolean = false;
        let orderFirst: boolean = false;
        if(owner){
            addUrl += "?owner=" + owner;
            ownerFirst = true;
        }else if(category){
            addUrl += "?category=" + category;
            catFirst = true;
        }else if(order){
            addUrl += "?orderBy=" + order;
            limFirst = true;
        }else if(limit != 0){
            addUrl += "?limit=" + limit;
            orderFirst = true;
        }

        if(owner && ownerFirst == false){
            addUrl += "&owner=" + owner;
        }

        if(category && catFirst == false){
            addUrl += "&category=" + category;
        }

        if(limit != 0 && limFirst == false){
            addUrl += "&limit=" + limit;
        }

        if(order && orderFirst == false){
            addUrl += "&orderBy=" + order;
        }
        return addUrl
    }

    getTodoById(id: string): Observable<Todo> {
        return this.http.request(this.todoUrl + "/" + id).map(res => res.json());
    }
}