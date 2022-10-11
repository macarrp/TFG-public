import { Component, OnInit } from '@angular/core';
import { TestService } from '../services/test.service';


@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.scss']
})
export class TestComponent implements OnInit {

  fileName = '';
  file: File;

  constructor(private readonly testService: TestService) { }

  ngOnInit(): void {
  }

  onFileSelected(event) {
    this.file = event.target.files[0];

    if (this.file) {
      this.fileName = this.file.name;

      // const formData = new FormData();
      // formData.append("thumbnail", file);
      // const upload$ = this.http.post("/api/thumbnail-upload", formData);
      // upload$.subscribe();

      console.log('nombreFich', this.fileName);
    }
  }

  uploadFile() {
    console.log('fich', this.file);
    this.testService.uploadFile(this.file);
  }

}
