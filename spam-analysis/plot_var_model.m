function plot_var_model(ii, initial, number_of_interval, interval, YAct, YPred, MAE, k, ylabel_name, restaurant_name)
    hfig = figure('Visible','off');
    h1 = plot(0:interval*3,YAct(initial+interval:end),'Color','g','LineWidth',1);
    hold on;
    maxY = ceil(max(max(max(YPred)),max(YAct)));

    for i=1:number_of_interval-1
        h2 = plot((i-1)*interval:i*interval,[YAct(initial+i*interval);YPred((i-1)*interval+1:i*interval,1)],':b','LineWidth',1.5);
        h3 = plot((i-1)*interval:i*interval,[YAct(initial+i*interval);YPred((i-1)*interval+1:i*interval,2)],'--r','LineWidth',1);
        h5 = plot(i*interval*ones(1,maxY*2+1),[0:0.5:maxY],'-','Color',[0 0 0],'LineWidth',1);
    end;

    xlabel('Period','FontName','Times New Roman','FontSize',9);
    axis([0 inf min(0.5,min(min(YPred))) maxY]);
    hAx = get(hfig,'CurrentAxes');
    set(hAx,'xTick',50*[0:3],'xTickLabel',50*[1:4]);

    set(hAx,'FontName','Times New Roman');
    set(hAx,'FontSize',9);
    set(hAx,'LineWidth',1);
    set(hAx,'Units','inches','Position',[1,1,1.9,1.9]);
    set(gca, 'LooseInset', [0,0,0,0]);

    h=hfig;
    set(h,'Units','Inches');
    pos = get(h,'Position');
    set(h,'PaperPositionMode','Auto','PaperUnits','Inches','PaperSize',[pos(3),pos(4)]);
    print(h,strcat('Cluster-',num2str(k),' Restaurant-',restaurant_name,' i-', num2str(ii)),'-dpdf','-r1000');
return;
