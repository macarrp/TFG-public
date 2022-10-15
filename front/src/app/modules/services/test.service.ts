import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TestService {

  private backendUrl: string = environment.backendUrl;


  constructor(private readonly http: HttpClient) { }

  async uploadFile(file) {
    const formData = new FormData();

    formData.append("file", file);

    const promise = new Promise((resolve, reject) => {
      this.http.post(`${this.backendUrl}test/kettle`, formData).subscribe()
    });

    // return 
  }
}
