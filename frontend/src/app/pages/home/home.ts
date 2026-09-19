import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Carousel } from 'bootstrap';

@Component({
  selector: 'app-home',

  imports: [
    RouterLink
  ],

  templateUrl: './home.html',

  styleUrl: './home.scss'
})
export class Home implements AfterViewInit {
  @ViewChild('heroCarousel') heroCarouselRef!: ElementRef;

  ngAfterViewInit(): void {
    new Carousel(this.heroCarouselRef.nativeElement, {
      interval: 4000,
      ride: 'carousel',
      pause: false
    });
  }
}