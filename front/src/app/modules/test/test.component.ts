import { Component, OnInit } from '@angular/core';
import { TestService } from '../../services/test.service';


@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.scss']
})
export class TestComponent implements OnInit {

  
  
  constructor(private readonly testService: TestService) { }

  ngOnInit(): void {
  }

}
