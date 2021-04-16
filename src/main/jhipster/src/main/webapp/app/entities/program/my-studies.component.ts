import { AfterViewInit, Component, ElementRef, isDevMode, QueryList, ViewChildren } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { switchMap, debounceTime, takeUntil, repeat, map } from 'rxjs/operators';
import { MyStudy, MyStudyMetadata, NgChartsBarPlotMetadata, ObservationsMetadata } from './my-study';
import { empty, fromEvent, Subject, of } from 'rxjs';
import { MyStudiesService } from './my-studies.service';

@Component({
    selector: 'jhi-my-studies',
    templateUrl: './my-studies.component.html'
})
export class MyStudiesComponent {
    programUUID: string;
    cropName: string;

    mouseEnter = new Subject();
    mouseLeave = new Subject();

    // ngx-charts options

    /*
     * FIXME dev mode issues (not happening in prod)
     *  - responsive (view=undefined) (use default->600,400 as workaround)
     *   https://github.com/swimlane/ngx-charts/issues/374
     *  - ERROR TypeError: Cannot read property 'runOutsideAngular' of undefined
     *  - tooltip icons not rendering correctly
     *  - PLEASE NOTE in dev mode the chart will overflow in small screens
     */
    view: any[] = isDevMode() ? [600, 400] : undefined;
    showXAxis = true;
    showYAxis = true;
    gradient = false;
    showLegend = true;
    showXAxisLabel = true;
    xAxisLabel = 'environment';
    showYAxisLabel = true;
    yAxisLabel = 'count';
    animations = true;
    colorScheme = {
        domain: ['#f7912f', '#f7b62f', '#ffffff']
    };

    studies: MyStudy[] = [];
    study: MyStudy;

    page = 1;
    pageSize = 10;
    totalCount: any;
    isLoading = false;

    constructor(
        private route: ActivatedRoute,
        private myStudiesService: MyStudiesService
    ) {
        this.route.queryParams.subscribe((params) => {
            this.cropName = params['cropName'];
            this.programUUID = params['programUUID'];
            this.load();
        });

        this.mouseEnter.pipe(
            debounceTime(100),
            takeUntil(this.mouseLeave),
            repeat()
        ).subscribe((study: MyStudy) => {
            this.select(study);
        })
    }

    load() {
        this.myStudiesService.getMyStudies(
            this.page - 1,
            this.pageSize,
            this.cropName,
            this.programUUID
        ).pipe(map((resp) => {
            // TODO
            // this.totalCount = resp.headers.get('X-Total-Count')
            this.totalCount = 50;
            return resp.body.map((study) => <MyStudy>({
                name: study.name,
                type: study.type,
                date: study.date,
                folder: study.folder,
                metadata: this.transformMetadata(study)
            }));
        })).subscribe((studies) => {
            this.studies = studies;
            this.select(this.studies[0]);
        });
    }

    private transformMetadata(study: MyStudy) {
        if (!(study.metadata && study.metadata.observations)) {
            return {};
        }
        // TODO combine datasets
        const observationSeries = study.metadata.observations.map((obs: ObservationsMetadata) => {
            return <NgChartsBarPlotMetadata>({
                name: obs.instanceName,
                series: [{
                    name: 'confirmed',
                    value: obs.confirmedCount
                }, {
                    name: 'pending',
                    value: obs.pendingCount
                }, {
                    name: 'unobserved',
                    value: obs.unobservedCount
                }]
            });
        });
        return <MyStudyMetadata>({
            observations: observationSeries
        });
    }

    onMouseEnter(study) {
        this.mouseEnter.next(study);
    }

    onMouseLeave() {
        this.mouseLeave.next();
    }

    select(study: MyStudy) {
        this.studies.forEach((s) => s.selected = false);
        study.selected = true;
        this.study = study;
    }
}
