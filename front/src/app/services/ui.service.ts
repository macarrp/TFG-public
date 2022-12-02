import {ApplicationRef, ComponentFactoryResolver, ComponentRef, EmbeddedViewRef, Injectable, Injector} from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Dom } from '../core/Dom';
import { SpinnerComponent } from '../shared/components/spinner/spinner.component';

@Injectable({
  providedIn: 'root'
})
export class UiService {

  counter = 0;

  private spinner: ComponentRef<SpinnerComponent>;

  constructor(
    private readonly componentFactoryResolver: ComponentFactoryResolver,
    private readonly appRef: ApplicationRef,
    private readonly modalService: NgbModal,
    private readonly injector: Injector
  ) {}

  createComponentRef(component: any): ComponentRef<any> {
    const componentRef = this.componentFactoryResolver
        .resolveComponentFactory(component)
        .create(this.injector);
    this.appRef.attachView(componentRef.hostView);
    return componentRef;
  }

  destroyRef(componentRef: ComponentRef<any>, delay = 0): void {
    setTimeout(() => {
        this.appRef.detachView(componentRef.hostView);
        componentRef.destroy();
    }, delay);
  }

  getDomElementFromComponentRef(componentRef: ComponentRef<any>): HTMLElement {
    return (componentRef.hostView as EmbeddedViewRef<any>)
        .rootNodes[0] as HTMLElement;
  }

  showSpinner(message = 'Cargando'): void {
    if (!this.spinner) {
      this.spinner = this.createComponentRef(SpinnerComponent);
      Dom.addChild(this.getDomElementFromComponentRef(this.spinner));
    }
    this.spinner.instance.message = message;
  }

  showSpinnerCounter(message?: string) {
    console.log('showSpinnerCounter: '+this.counter);
    this.showSpinner(message);
    this.counter++;
  }

  hideSpinner(): void {
    if (this.spinner) {
      this.destroyRef(this.spinner);
      delete this.spinner;
    }
    this.counter = 0;
  }

  hideSpinnerCounter() {
    this.counter--;
    console.log('hideSpinnerCounter: '+this.counter);
    if (!this.counter) {
      this.hideSpinner();
    }
  }
}