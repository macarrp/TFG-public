import { Component, OnInit } from '@angular/core';
import { TestService } from '../services/test.service';

@Component({
  selector: 'app-kettle',
  templateUrl: './kettle.component.html',
  styleUrls: ['./kettle.component.scss']
})
export class KettleComponent implements OnInit {

  fileName = '';
  file: File;

  errorMessage: string = null;
  successMessage: string = null;

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
    if (this.fileName == null || this.fileName == undefined)
      return;

    this.testService.uploadFile(this.file)
      .then((response: string) => {
        console.log('response met', response);
        this.successMessage = response;
      })
      .catch((err) => {
        console.log('err', err);
        this.errorMessage = err;
      })
  }

}
