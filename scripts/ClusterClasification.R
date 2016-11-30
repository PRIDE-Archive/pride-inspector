#!/usr/bin/env Rscript

library(corrplot)
library(fitdistrplus)
library(mclust)
library(ggplot2)
library(optparse)

option_list = list(
    make_option(
           c("-f", "--file"),
           type="character",
           default=NULL,
           help="Input File to be Analysed",
           metavar="character"),
    make_option(
           c("-o", "--out"),
           type="character",
           default="out.tsv",
           help="output file name [default= %default]",
           metavar="character"),
    make_option(
        c("-p", "--plotName"),
        type="character",
        default="analysis-details",
        help="plot Name file name [default= %default]",
        metavar="character"),
    make_option(
        c("-a", "--folderAnalysis"),
        type="character",
        default=NULL,
        help="Folder where the analysis will be.",
        metavar="character")
);

opt_parser = OptionParser(option_list=option_list);
opt = parse_args(opt_parser);

if (is.null(opt$file) || is.null(opt$folderAnalysis) || is.null(opt$out)){
    print_help(opt_parser)
    stop("At least one argument must be supplied (input file).n", call.=FALSE)
}

projects <- read.delim(file = opt$file, header = TRUE)
projects[is.na(projects)] <- 0

projects$diff <- (1 - projects$INCORRECT_SPECTRA/projects$SPECTRA_IN_REL_CLUSTERS)
projects$diffCont <- (1- projects$CONTAMINANT_INCORRECT_SPECTRA/projects$SPECTRA_IN_REL_CLUSTERS)

values <- projects

values <- values[order(-values$diff),]
values$order <- seq.int(nrow(values))

clust <- kmeans(values$diff, centers = 4, iter.max = 10, nstart = 1,
                algorithm = c("Lloyd"), trace=FALSE)

png(filename= paste(opt$folderAnalysis,"/", opt$plotName, "-cluster.png", collapse = NULL, sep = ""))
plot(values$diff, col = clust$cluster, xlab = "Order of Projects in the Cluster", ylab = "Realiability Score")
dev.off()

values$class <- clust$cluster

current = values$class[1]
values$NewClass[1] = 1
class = 1
for(count in  2:nrow(values) ){
    if(values$class[count] == current){
        values$NewClass[count] = class
    }else{
        class = class + 1
        values$NewClass[count] = class
        current = values$class[count]
    }
}

values$class <- NULL

names(values)[names(values)=="NewClass"] <- "class"


png(filename = paste(opt$folderAnalysis,"/", opt$plotName, "-density.png", collapse = NULL, sep = ""))
plotdist(values$diff, histo = TRUE, demp = TRUE)
dev.off()

write.table(values, opt$out, sep="\t", quote=FALSE)



