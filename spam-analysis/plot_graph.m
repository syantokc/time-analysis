function plot_graph(ksc, cent, cluster_number, t, review, type)
    res_count = zeros(1,cluster_number);
    review_count = zeros(1,cluster_number);
    ct_index = 1;
    for index = ksc'
        res_count(index) = res_count(index) + 1;
        review_count(index) = review_count(index) + review(ct_index);
        ct_index = ct_index + 1;
    end;

    new_cent = zeros(size(cent));
    for j = 1 : cluster_number
        if max(cent(j,:)) > 0
            new_cent(j,:) = cent(j,:)/(max(cent(j,:)));
        end;
    end;

    y_label = 'Normalized Review Count';
    if type == 0
        y_label = 'Normalized Rating';
    end;
    for i=1:cluster_number

        figure('Name',strcat(t,'Cluster: ', num2str(i)),'visible','off');
        plot([1:size(cent,2)],new_cent(i,:));

        axis([0 size(cent,2) 0 1]);
        opt = [];
        opt.XLabel = 'Number of Months';   % xlabel
        opt.YLabel = y_label; % ylabel
        opt.BoxDim = [0.75, 1.0];
        opt.Title = strcat('Restaurants-',num2str(res_count(i)),' Reviews-',num2str(review_count(i)));
        opt.FileName = strcat(t,' Cluster-', num2str(i),'.png');
        opt.FontSize = 7;
        opt.XTick = 20 * [0:size(cent,2)/20];
        opt.AxisLineWidth = 1.0;
        opt.LineWidth  = 1.0;
        setPlotProp(opt);
    end

return;
