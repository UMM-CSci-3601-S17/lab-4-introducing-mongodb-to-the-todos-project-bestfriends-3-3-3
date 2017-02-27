import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Todo } from './todo';
import { Observable } from "rxjs";

@Injectable()
export class TodoListService {
    private todoUrl: string = API_URL + "todos";
    constructor(private http:Http) { }

    getTodos(owner: string, category: string, status: string, limit : number, order : string): Observable<Todo[]> {
        let tempUrl : string = this.urlBuilder(owner,category,status, limit, order);
        return this.http.request(tempUrl).map(res => res.json());
    }

    urlBuilder(owner: string, category: string, status: string, limit : number, order : string): string{
        let addURL : string = this.todoUrl;
        if(owner){
            addURL += "?owner=" + owner;
        }

        if(owner && category){
            addURL += "&category=" + category;
        }else if(owner == null && category){
            addURL = "?category=" + category;
            return addURL;
        }

        if(owner && category && status){
            addURL += "&status=" + status;
        }else if(!owner && category && status){
            addURL += "&status=" + status;
        }else if(!owner && !category && status){
            addURL += "?status=" + status;
        }

        if(owner && category && status && limit != 0 || !owner && category && status && limit != 0 ||
            !owner && !category && status && limit != 0 ){
            addURL += "&limit=" + limit;
        }else if (!owner && !category && !status && limit != 0 && !order){
            addURL += "?limit=" + limit;
        }

        if(owner && category && status && limit != 0 && order|| !owner && category && status && limit != 0 && order||
            !owner && !category && status && limit != 0 && order || !owner && !category && !status && limit != 0 && order){
            addURL += "&orderBy=" + order;
        }else if (!owner && !category && !status && limit == 0 && order){
            addURL += "?orderBy=" + order;
        }


        if(!owner && !category && !status && limit == 0 && !order){
            return addURL;
        } //for no filter at all

        return addURL
    }

    getTodoById(id: string): Observable<Todo> {
        return this.http.request(this.todoUrl + "/" + id).map(res => res.json());
    }
}