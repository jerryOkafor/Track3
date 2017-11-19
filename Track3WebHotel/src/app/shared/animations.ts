import { animate, keyframes, style, transition, trigger } from '@angular/animations';

export class ILSTransitions {

  static pageTransition =
    trigger('transition', [
      transition('void => *', [
        style({opacity: 0}),
        animate('500ms', style({opacity: 1}))
      ]),
      transition('*=>void', [
          style({'opacity': 1}),
          animate('500ms', style({opacity: 0}))
        ]
      )
    ]);

  static errorEnter =
    trigger('errorEnter', [
      transition('void => *', [
        style({
          transform: 'translateY(-100%)',
          opacity: 0
        }),
        animate('300ms ease-in')
      ]),
      transition('* => void', [
        style({
          transform: 'scaleY(0) translateY(100%)',
          opacity: 1
        }),
        animate('300ms ease-out')
      ])
    ]);

  static itemShow =
    trigger('itemShow', [
      transition('void => *', [
        animate(300, keyframes([
          style({opacity: 0, transform: 'translateY(0)', offset: 0}),
          style({opacity: 0.6, transform: 'translateY(-15px)', offset: 0.6}),
          style({opacity: 1, transform: 'translateY(0)', offset: 1}),
        ]))
      ]),
      transition('* => void', [
        animate(300, keyframes([
          style({opacity: 1, transform: 'translateY(0)', offset: 0}),
          style({opacity: 0.1, transform: 'translateY(-15px)', offset: 0.6}),
          style({opacity: 0, transform: 'translateY(100%)', offset: 1}),
        ]))
      ])
    ]);


  static slideFromLeft =
    trigger('slideFromLeft', [
      transition('void => *', [
        animate(300, keyframes([
          style({opacity: 0, transform: 'translateX(-10%)', offset: 0}),
          style({opacity: 0.5, transform: 'translateX(-5%)', offset: 0.6}),
          style({opacity: 1, transform: 'translateX(0)', offset: 1}),
        ]))
      ]),
      transition('* => void', [
        animate(300, keyframes([
          style({opacity: 1, transform: 'translateX(0)', offset: 0}),
          style({opacity: 0.2, transform: 'translateX(4%)', offset: 0.6}),
          style({opacity: 0, transform: 'translateX(100%)', offset: 1}),
        ]))
      ])
    ]);

}

