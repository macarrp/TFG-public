import { Component, OnInit } from '@angular/core';
import { KettleService } from 'src/app/services/kettle.service';

@Component({
  selector: 'app-kettle',
  templateUrl: './kettle.component.html',
  styleUrls: ['./kettle.component.scss']
})
export class KettleComponent implements OnInit {

  // Job principal
  file: File;

  // Adjuntos
  fileList: FileList;
  llevaAdjuntos: boolean = false;

  // Mensajes
  errorMessage: string = null;
  successMessage: string = null;
  logEjecucionKettle: string = null;

  origenDatos = ['Excel', 'Texto plano', 'BD', 'XML'];
  origenDatosSelected: string;

  constructor(private readonly kettleService: KettleService) { }

  ngOnInit(): void {
  }

  onFileSelected(event) {
    this.file = event.target.files[0];
  }

  onMultipleFilesSelected(event) {
    this.fileList = event.target.files;
  }

  uploadFile() {
    if (this.file == null || this.file == undefined) {
      this.errorMessage = 'Debes seleccionar un archivo'
      return;
    }

    this.cleanLogs();
    
    if(!this.llevaAdjuntos) {
      this.uploadKettleTransformation();
    } 
    else {
      this.uploadKettleTransformationWithAttachments();
    }
  }

  uploadKettleTransformation() {
    this.kettleService.uploadKettleTransformation(this.file).subscribe((result) => {
      if(result.success) {

        if(result.message.errores == 0) {
          this.successMessage = result.message.mensaje;
          this.logEjecucionKettle = result.message.log;
        }
        else {
          this.errorMessage = result.message.mensaje;
        }

      }
      else {
        this.errorMessage = result.error;
      }
      // Éxito o no, deberíamos recibir logs (de momento solo éxito)
      // this.logEjecucionKettle = result.message.log;
      // this.crearLogKettle(); Si quiero que se descargue automaticamente
    })
  }

  uploadKettleTransformationWithAttachments() {
    this.kettleService.uploadKettleTransformationWithAttachments(this.file, this.fileList).subscribe((result) => {
      if(result.success) {

        if(result.message.errores == 0) {
          this.successMessage = result.message.mensaje;
          this.logEjecucionKettle = result.message.log;
        }
        else {
          this.errorMessage = result.message.mensaje;
        }
      }
      else {
        this.errorMessage = result.error;
      }
    })
  }

  crearLogsKettle() {
    let blob = new Blob([this.logEjecucionKettle], {type: "text/plain;charset=utf-8"});
    let blobUrl = URL.createObjectURL(blob);

    let link = document.createElement('a');
    link.setAttribute('type', 'hidden');

    link.href = blobUrl;
    link.download = "kettleLogs.txt";

    document.body.appendChild(link);
    link.click();
    link.remove();
  }

  cleanLogs() {
    this.errorMessage = '';
    this.successMessage = '';
  }

}
