import { Component, OnInit } from '@angular/core';
import { LogLevelKettle } from 'src/app/models/enums/LogLevelKettle.enum';
import { KettleResponse } from 'src/app/models/KettleResponse.model';
import { ObjectResponse } from 'src/app/models/ObjectResponse.model';
import { KettleService } from 'src/app/services/kettle.service';

@Component({
  selector: 'app-kettle',
  templateUrl: './kettle.component.html',
  styleUrls: ['./kettle.component.scss']
})
export class KettleComponent implements OnInit {

  // Kettle principal
  file: File;

  // Adjuntos
  fileList: FileList;
  llevaAdjuntos: boolean = false;

  // Mensajes
  errorMessage: string = null;
  successMessage: string = null;
  logEjecucionKettle: string = null;

  // LogLevel
  keys = Object.keys;
  logsLevelKettle = LogLevelKettle;
  logLevelKettleSelected: LogLevelKettle = LogLevelKettle.BASIC;

  // Loading spinner
  loading: boolean = false;

  constructor(private readonly kettleService: KettleService) { }

  ngOnInit(): void {
  }

  onFileSelected(event) {
    this.file = event.target.files[0];
    this.cleanLogs();
  }

  onMultipleFilesSelected(event) {
    this.fileList = event.target.files;
    this.cleanLogs();
  }

  uploadFile() {
    if (this.file == null || this.file == undefined) {
      this.errorMessage = 'Debes seleccionar un archivo'
      return;
    }

    this.cleanLogs();
    this.uploadKettleTransformation();
  }

  uploadKettleTransformation() {
    this.loading = true;
    this.kettleService.uploadKettleTransformation(this.file, this.fileList, this.logLevelKettleSelected).subscribe(
      (result: ObjectResponse<KettleResponse>) => {
        if (result.success) {
          if (result.message.errores == 0)
            this.successMessage = result.message.mensaje;
          else
            this.errorMessage = result.message.mensaje;

          this.logEjecucionKettle = result.message.log;
        }
        else {
          this.errorMessage = result.error;
        }
        this.loading = false;
      },
      (error) => {
        this.errorMessage = 'Error al lanzar transformación';
        this.loading = false;
      })
  }

  crearLogsKettle() {
    let blob = new Blob([this.logEjecucionKettle], { type: "text/plain;charset=utf-8" });
    let blobUrl = URL.createObjectURL(blob);

    let link = document.createElement('a');
    link.setAttribute('type', 'hidden');

    link.href = blobUrl;
    link.download = this.file.name + "_kettleLogs.txt";

    document.body.appendChild(link);
    link.click();
    link.remove();
  }

  cleanLogs() {
    this.errorMessage = '';
    this.successMessage = '';
  }

  logVar(variable) {
    console.log('valor: ', variable)
  }

}
