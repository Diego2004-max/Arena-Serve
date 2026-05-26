import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  template: `<div class="dialog" *ngIf="visible"><p>{{message}}</p><button (click)="confirm.emit()">Aceptar</button><button (click)="cancel.emit()">Cancelar</button></div>`,
  styles: [`.dialog{background:#fff;border:1px solid #dce3ee;border-radius:8px;padding:16px;box-shadow:0 10px 30px #10233f22}button{margin-right:8px}`]
})
export class ConfirmDialogComponent {
  @Input() visible = false;
  @Input() message = '';
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
}
