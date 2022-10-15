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

  errorMessage = '';
  successMessage = '';

  constructor(private readonly testService: TestService) { }

  ngOnInit(): void {
  }

  onFileSelected(event) {
    this.file = event.target.files[0];

    if (this.file) {
      this.fileName = this.file.name;
    }
  }

  uploadFile() {
    if(this.fileName == null || this.fileName == undefined)
      return;

    this.testService.uploadFile(this.file);
  }

}
