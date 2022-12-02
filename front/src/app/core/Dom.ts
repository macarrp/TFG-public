export class Dom {
  static removeChildById(id: string, parent: HTMLElement = document.body): void {
    const el = document.getElementById(id);
    if (!(el === null || el === undefined) && !(el.parentElement === null || el.parentElement === undefined)) {
      el.parentElement.removeChild(el);
    }
  }

  static removeChild(child: string | HTMLElement, parent: HTMLElement = document.body): void {
    if (child instanceof HTMLElement) {
      parent.removeChild(child);
    } else {
      this.removeChildById(child, parent);
    }
  }

  static addChild(child: HTMLElement, parent: HTMLElement = document.body): void {
    parent.appendChild(child);
  }

  private static async addScript(child: HTMLScriptElement, parent: HTMLElement = document.body): Promise<boolean> {
    return new Promise((resolve, reject) => {
      child.addEventListener('load', () => {
        resolve(true);
      });

      child.addEventListener('error', () => {
        reject(new Error(`${child.src} failed to load.`));
      });

      parent.appendChild(child);
    });
  }

  static async addScriptbyUrl(url: string, parent: HTMLElement = document.body, id?: string): Promise<boolean> {

    if(!(id === null || id === undefined) && id.trim() !== '' && document.getElementById(id)){
        return new Promise((resolve, _) => {
            resolve(false);
        });
    }

    const script = document.createElement('script');
    script.type = 'text/javascript';
    if (!(id === null || id === undefined) && id.trim() !== '') {
      script.id = id;
    }
    script.async = false;
    script.src = url;
    return Dom.addScript(script, parent);
  }
}