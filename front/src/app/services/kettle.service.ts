import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { KettleResponse } from '../models/KettleResponse.model';
import { ObjectResponse } from '../models/ObjectResponse.model';

@Injectable({
  providedIn: 'root'
})
export class KettleService {

  private backendUrl: string = environment.backendUrl;

  constructor(private readonly http: HttpClient) { }

  uploadKettleTransformation(file: File): Observable<ObjectResponse<KettleResponse>> {
    const formData = new FormData();

    formData.append("kettle", file);

    return this.http.post<ObjectResponse<KettleResponse>>(`${this.backendUrl}kettle/transformation`, formData);
  }

  uploadKettleTransformationWithAttachments(file: File, files: FileList): Observable<ObjectResponse<KettleResponse>> {
    const formData = new FormData();

    formData.append("kettle", file)

    for(let i = 0; i < files.length; i++) {
      formData.append("files", files.item(i))
    }

    return this.http.post<ObjectResponse<KettleResponse>>(`${this.backendUrl}kettle/transformation-with-attachments`, formData);
  }

  uploadFile(file: File): Observable<ObjectResponse<KettleResponse>> {
    const formData = new FormData();

    formData.append("kettle", file);

    return this.http.post<ObjectResponse<KettleResponse>>(`${this.backendUrl}kettle/transformation`, formData);
  }
}
